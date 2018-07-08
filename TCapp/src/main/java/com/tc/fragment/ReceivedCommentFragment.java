package com.tc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pb.myapplication.MainActivity;
import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.tc.activity.TcContentDetailActivity;
import com.tc.adapter.ReceivedCommentAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.data.TcData;
import com.tc.model.ReceivedComment;
import com.tc.utils.CommonUtil;
import com.tc.utils.Report;
import com.tc.utils.StringUtil;
import com.tc.view.CommonDialog;
import com.tc.view.Windows;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 收到的评论
 * Created by PB on 2018/1/25.
 */
@ContentView(R.layout.activity_common_list)
public class ReceivedCommentFragment extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @ViewInject(R.id.layout_input)
    LinearLayout layout_input;

    private ReceivedCommentAdapter adapter;
    private List<ReceivedComment> datas;


    @ViewInject(R.id.btn_send)
    Button btn_send;
    @ViewInject(R.id.edt_content)
    EmojiEditText edt_content;
    //    @ViewInject(R.id.view_pager)
//    ExpressionViewPager expressionViewPager;

    @ViewInject(R.id.iv_emotion_pressed)
    ImageView iv_emotion_pressed;
    @ViewInject(R.id.iv_emotion)
    ImageView iv_emotion;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;
    @ViewInject(R.id.header)
    private View header;
    Activity activity;

    @ViewInject(R.id.rootView)
    View rootView;
    EmojiPopup emojiPopup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
        initData();
    }

    ReceivedComment receivedComment;

    private void initData() {
        datas = new ArrayList<>();
        adapter = new ReceivedCommentAdapter(activity, datas);
        getData(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        initView();
        return view;
    }

    private void initView() {
        header.setVisibility(View.GONE);
        listView.setAdapter(adapter);
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);

            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当没有可加载的内容，关闭加载更多
                if (totalItemCount >= totalNum) {//totalItemCount-header
                    smartRefreshLayout.setLoadmoreFinished(true);
                }
            }
        });
        getData(true);
        reportTypeSelectDialog = Windows.createSelectReportTypeDialog(activity, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                funSelectDialog.show();
                receivedComment = datas.get(position);
            }
        });
        createSelectFunDialog();

        btn_send.setOnClickListener(this);
        iv_emotion.setOnClickListener(this);
        iv_emotion_pressed.setOnClickListener(this);

//        expressionViewPager.setVisibility(View.GONE);
//        expressionViewPager.setEditText(edt_content);
        iv_emotion_pressed.setVisibility(View.GONE);
        edt_content.setOnClickListener(this);
        edt_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    expressionViewPager.setVisibility(View.GONE);
                    emojiPopup.dismiss();
                    iv_emotion_pressed.setVisibility(View.GONE);
                    iv_emotion.setVisibility(View.VISIBLE);
                }
            }
        });
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(edt_content);
        setOnTouchListener();
//        emojiPopup.dismiss();
    }

    int pageNum = 1;
    int totalNum = 10;

    private void getData(final boolean refresh) {
        pageNum = refresh ? 1 : pageNum;
        Log.i("pageNum", pageNum + "");
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getCommentInfo");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addParameter("pageNum", pageNum);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("sucess", result);
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<ReceivedComment>>>() {
                }.getType();
                ResponseResult<List<ReceivedComment>> responseResult = gson.fromJson(result, jsonType);
                if (responseResult.getCode() == Code.SUCCESS) {
                    List<ReceivedComment> tempData = responseResult.getData();
                    if (refresh) {
                        datas.clear();
                        smartRefreshLayout.setLoadmoreFinished(false);
                        smartRefreshLayout.finishRefresh();
                        //如果当前是第一页，则获取下一页数据
                        if (pageNum == 1)
                            pageNum++;
                    } else {
                        pageNum++;
                        smartRefreshLayout.finishLoadmore(0);
                    }
                    datas.addAll(tempData);
                    adapter.notifyDataSetChanged();

                    if (datas.size() == 0) {
                        layout_empty.setVisibility(View.VISIBLE);
                        layout_list.setVisibility(View.GONE);
                    } else {
                        layout_empty.setVisibility(View.GONE);
                        layout_list.setVisibility(View.VISIBLE);
                    }
                    try {
                        totalNum = Integer.parseInt(responseResult.getMessage());
                        Log.i("SquareFrag--->totalNum:", totalNum + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("error", ex.toString());
                CommonUtil.toast(activity, Constant.NETWORK_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    private CommonDialog funSelectDialog;

    private TextView tv_reply;
    private TextView tv_report;
    private TextView tv_source;
    private LinearLayout ll_divider;
    public TextView tv_reply_anonymous;

    private void createSelectFunDialog() {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_function_comment, null);
        tv_reply = (TextView) view.findViewById(R.id.tv_reply);
        tv_report = (TextView) view.findViewById(R.id.tv_report);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        view.findViewById(R.id.ll_divider).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_delete).setVisibility(View.GONE);
        tv_reply_anonymous = (TextView) view.findViewById(R.id.tv_reply_anonymous);
        tv_reply_anonymous.setOnClickListener(this);
        tv_source.setVisibility(View.VISIBLE);
        tv_reply.setOnClickListener(this);
        tv_source.setOnClickListener(this);
        tv_report.setOnClickListener(this);
        funSelectDialog = new CommonDialog(activity, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    CommonDialog reportTypeSelectDialog;
    int anonymous;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_report:
                funSelectDialog.dismiss();
                reportTypeSelectDialog.show();
                break;
            case R.id.tv_source:
                funSelectDialog.dismiss();
                Intent intent = new Intent(activity, TcContentDetailActivity.class);
                intent.putExtra("content", receivedComment.getTcContent());
                startActivity(intent);
                break;
            case R.id.tv_reply:
                anonymous = 0;
                funSelectDialog.dismiss();
                layout_input.setVisibility(View.VISIBLE);
                setReplyInfo(receivedComment);
                break;
            case R.id.tv_reply_anonymous:
                anonymous = 1;
                layout_input.setVisibility(View.VISIBLE);
                setReplyInfo(receivedComment);
                funSelectDialog.dismiss();
                break;
            case R.id.tv_divulge_privacy:
            case R.id.tv_personal_attack:
            case R.id.tv_obscenity:
            case R.id.tv_adv:
            case R.id.tv_false_information:
            case R.id.tv_llegal_information:
            case R.id.tv_other:
                reportTypeSelectDialog.dismiss();
                Report.report(activity, v, receivedComment.getCommentId(), receivedComment.getTcContent().getContentId());
                break;
            case R.id.btn_send:
                checkInfo();
                sendComment(receivedComment.getTcContent().getContentId(), strContent, anonymous);
                break;
            case R.id.iv_emotion:
//                CommonUtil.hideVirtualKey(activity);
                emojiPopup.toggle();
//                CommonUtil.hideKeyBoard(activity, v.getWindowToken());
//                expressionViewPager.setVisibility(View.VISIBLE);
                iv_emotion_pressed.setVisibility(View.VISIBLE);
                iv_emotion.setVisibility(View.GONE);
                break;
            case R.id.edt_content:
                emojiPopup.dismiss();
//                expressionViewPager.setVisibility(View.GONE);
                iv_emotion_pressed.setVisibility(View.GONE);
                iv_emotion.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_emotion_pressed:
//                expressionViewPager.setVisibility(View.GONE);
                emojiPopup.dismiss();
                iv_emotion_pressed.setVisibility(View.GONE);
                iv_emotion.setVisibility(View.VISIBLE);
                break;
        }
    }

    String toUserId;
    String toCommentContent;
    String toCommentId;
    String toNickName;

    private void setReplyInfo(ReceivedComment receivedComment) {

        emojiPopup.dismiss();
        iv_emotion_pressed.setVisibility(View.GONE);
        iv_emotion.setVisibility(View.VISIBLE);

        String name = "匿名";
        if (receivedComment.getCommentAnonymous() == 0) {
            name = receivedComment.getCommentNickName();
            toNickName = receivedComment.getCommentNickName();
        } else {
            //如果回复匿名评论的评论，则将被评论的昵称设置为匿名
            toNickName = "匿名";
        }
        if (anonymous == 0)
            edt_content.setHint("回复 " + name);
        else if (anonymous == 1)
            edt_content.setHint("匿名回复 " + name);
        edt_content.setText("");
        edt_content.requestFocus();
        toUserId = receivedComment.getFromUserId();

        toCommentContent = receivedComment.getCommentContent();
        toCommentId = receivedComment.getCommentId();
        CommonUtil.manageInputMethod(activity);
    }


    String strContent;

    private void checkInfo() {
        strContent = edt_content.getText().toString();
        if (StringUtil.isEmpty(strContent)) {
            CommonUtil.toast(activity, "内容不能为空噢！");
            return;
        }

    }

    private void sendComment(String contentId, String strContent, int anonymous) {

        RequestParams params = TcData.sendComment(contentId,strContent,toUserId,toCommentContent,toCommentId,toNickName,anonymous);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult responseResult = gson.fromJson(result, ResponseResult.class);
                if (responseResult.getCode() == 1) {
                    CommonUtil.toast(activity, "评论成功");
//                    CommonUtil.hideKeyBoard(activity, edt_content.getWindowToken());
                    emojiPopup.dismiss();
                    iv_emotion.setVisibility(View.VISIBLE);
                    iv_emotion_pressed.setVisibility(View.GONE);
                    layout_input.setVisibility(View.GONE);
                } else if (responseResult.getCode() == Code.PROHIBIT) {
                    CommonUtil.toast(activity, "您已被禁言");
                }else if (responseResult.getCode() == Code.ILLEGAL_CONTENT) {
                    CommonUtil.toast(activity, "您发布的信息包含敏感词汇，禁止发送");
                } else {
                    CommonUtil.toast(activity, "评论失败,错误代码" + responseResult.getCode());
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    MainActivity.MyTouchListener myTouchListener;

    //处理layout_inout以外区域的触摸事件,隐藏软键盘与表情面板
    private void setOnTouchListener() {
        myTouchListener = new MainActivity.MyTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    View v = activity.getCurrentFocus();
                    if (CommonUtil.isShouldHideKeyboard(v, ev)) {
                        CommonUtil.hideKeyBoard(activity, v.getWindowToken());
                        emojiPopup.dismiss();
                    }
                }
            }
        };
        ((MainActivity) activity).registerMyOnTouchListener(myTouchListener);
    }

    @Override
    public void onDestroy() {
        ((MainActivity) activity).unRegisterMyOnTouchListener(myTouchListener);
        super.onDestroy();
    }
}
