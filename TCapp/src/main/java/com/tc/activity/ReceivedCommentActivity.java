package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
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
import com.tc.view.HeaderHolder;
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
 * Created by PB on 2017/11/29.
 */
@ContentView(R.layout.activity_common_list)
public class ReceivedCommentActivity extends BaseActivity implements View.OnClickListener {

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
    @ViewInject(R.id.iv_emotion)
    ImageView iv_emotion;
    @ViewInject(R.id.iv_emotion_pressed)
    ImageView iv_emotion_pressed;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;

    EmojiPopup emojiPopup;
    @ViewInject(R.id.rootView)
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    ReceivedComment receivedComment;

    private void init() {
        new HeaderHolder().init(this, "收到的评论").hideRightText();
        datas = new ArrayList<>();
        adapter = new ReceivedCommentAdapter(this, datas);
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
        reportTypeSelectDialog = Windows.createSelectReportTypeDialog(this, this);

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

        iv_emotion_pressed.setVisibility(View.GONE);
        edt_content.setOnClickListener(this);
        edt_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emojiPopup.dismiss();
                    iv_emotion_pressed.setVisibility(View.GONE);
                    iv_emotion.setVisibility(View.VISIBLE);
                }
            }
        });
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(edt_content);
        emojiPopup.dismiss();
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
                CommonUtil.toast(ReceivedCommentActivity.this, Constant.NETWORK_ERROR);
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
    private TextView tv_reply_anonymous;
    private LinearLayout ll_divider;

    private void createSelectFunDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_function_comment, null);
        tv_reply = (TextView) view.findViewById(R.id.tv_reply);
        tv_report = (TextView) view.findViewById(R.id.tv_report);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        tv_reply_anonymous = (TextView) view.findViewById(R.id.tv_reply_anonymous);
        view.findViewById(R.id.ll_divider).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_delete).setVisibility(View.GONE);
        tv_source.setVisibility(View.VISIBLE);
        tv_reply.setOnClickListener(this);
        tv_reply_anonymous.setOnClickListener(this);
        tv_source.setOnClickListener(this);
        tv_report.setOnClickListener(this);
        funSelectDialog = new CommonDialog(this, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
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
                Intent intent = new Intent(this, TcContentDetailActivity.class);
                intent.putExtra("content", receivedComment.getTcContent());
                startActivity(intent);
                break;
            case R.id.tv_reply_anonymous:
                anonymous = 1;
                funSelectDialog.dismiss();
                layout_input.setVisibility(View.VISIBLE);
                setReplyInfo(receivedComment);
                break;
            case R.id.tv_reply:
                anonymous = 0;
                funSelectDialog.dismiss();
                layout_input.setVisibility(View.VISIBLE);
                setReplyInfo(receivedComment);
                break;
            case R.id.tv_divulge_privacy:
            case R.id.tv_personal_attack:
            case R.id.tv_obscenity:
            case R.id.tv_adv:
            case R.id.tv_false_information:
            case R.id.tv_llegal_information:
            case R.id.tv_other:
                reportTypeSelectDialog.dismiss();
                Report.report(this, v, receivedComment.getCommentId(), receivedComment.getTcContent().getContentId());
                break;
            case R.id.btn_send:
                checkInfo();
                sendComment(receivedComment.getTcContent().getContentId(), strContent, anonymous);
                break;
            case R.id.iv_emotion:
                emojiPopup.toggle();
//                CommonUtil.hideKeyBoard(this, v.getWindowToken());
                iv_emotion_pressed.setVisibility(View.VISIBLE);
                iv_emotion.setVisibility(View.GONE);
                break;
            case R.id.edt_content:
                iv_emotion_pressed.setVisibility(View.GONE);
                emojiPopup.dismiss();
                iv_emotion.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_emotion_pressed:
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
        String name = "匿名";
        if (receivedComment.getCommentAnonymous() == 0)
            name = receivedComment.getCommentNickName();
        if (anonymous == 0)
            edt_content.setHint("回复 " + name);
        else if (anonymous == 1)
            edt_content.setHint("匿名回复 " + name);
        edt_content.setText("");
        edt_content.requestFocus();
        toUserId = receivedComment.getFromUserId();
        toNickName = name;
        toCommentContent = receivedComment.getCommentContent();
        toCommentId = receivedComment.getCommentId();
        CommonUtil.manageInputMethod(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (CommonUtil.isShouldHideKeyboard(v, ev)) {
                CommonUtil.hideKeyBoard(this, v.getWindowToken());
                iv_emotion_pressed.setVisibility(View.GONE);
                iv_emotion_pressed.setVisibility(View.VISIBLE);
                emojiPopup.dismiss();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    String strContent;

    private void checkInfo() {
        strContent = edt_content.getText().toString();
        if (StringUtil.isEmpty(strContent)) {
            CommonUtil.toast(this, "内容不能为空噢！");
            return;
        }

    }

    private void sendComment(String contentId, String strContent, int anonymous) {

//        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/releaseTcContentComment");
//        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
//        params.addQueryStringParameter("userId", App.USER_ID);
//        params.addQueryStringParameter("contentId", contentId);
//        params.addQueryStringParameter("content", strContent);
//        params.addQueryStringParameter("toUserId", toUserId);
//        params.addQueryStringParameter("toCommentContent", toCommentContent);
//        params.addQueryStringParameter("toCommentId", toCommentId);
//        params.addQueryStringParameter("toNickName", toNickName);
//        params.addQueryStringParameter("anonymous", anonymous + "");

        RequestParams params = TcData.sendComment(contentId,strContent,toUserId,toCommentContent,toCommentId,toNickName,anonymous);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult responseResult = gson.fromJson(result, ResponseResult.class);
                if (responseResult.getCode() == 1) {
                    CommonUtil.toast(ReceivedCommentActivity.this, "评论成功");
                    CommonUtil.hideKeyBoard(ReceivedCommentActivity.this, edt_content.getWindowToken());
//                    expressionViewPager.setVisibility(View.GONE);
                    iv_emotion.setVisibility(View.VISIBLE);
                    emojiPopup.dismiss();
                    iv_emotion_pressed.setVisibility(View.GONE);
                    layout_input.setVisibility(View.GONE);
                } else if (responseResult.getCode() == Code.PROHIBIT) {
                    CommonUtil.toast(ReceivedCommentActivity.this, "您已被禁言");
                } else if (responseResult.getCode() == Code.ILLEGAL_CONTENT) {
                    CommonUtil.toast(ReceivedCommentActivity.this, "您发布的信息包含敏感词汇，禁止发送");
                } else {
                    CommonUtil.toast(ReceivedCommentActivity.this, "评论失败,错误代码" + responseResult.getCode());
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
}
