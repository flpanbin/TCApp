package com.tc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.tc.adapter.CommentAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.TcData;
import com.tc.fragment.SquareFragment;
import com.tc.model.Comment;
import com.tc.model.TCContent;
import com.tc.utils.CommonUtil;
import com.tc.data.Constant;
import com.tc.utils.Report;
import com.tc.utils.StringUtil;
import com.tc.view.CommonDialog;
import com.tc.view.HeaderHolder;
import com.tc.adapter.holder.TcContentItemHolder;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PB on 2017/6/19.
 */
@ContentView(R.layout.activity_tc_content_detail)
public class TcContentDetailActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.lv_comment)
    private ListView lv_comment;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @ViewInject(R.id.btn_send)
    Button btn_send;

    @ViewInject(R.id.edt_content)
    EmojiEditText edt_content;
    private static final int SELECT_PHOTO = 0;

    //    @ViewInject(R.id.view_pager)
//    ExpressionViewPager expressionViewPager;
    @ViewInject(R.id.iv_emotion)
    ImageView iv_emotion;
    @ViewInject(R.id.iv_emotion_pressed)
    ImageView iv_emotion_pressed;

    View headerView;
    TCContent content;
    List<Comment> commentDatas;
    CommentAdapter commentAdapter;
    String strContent;
    TcContentItemHolder holder;

    String contentId;

    EmojiPopup emojiPopup;
    @ViewInject(R.id.rootView)
    View rootView;

    @ViewInject(R.id.ll_content)
    LinearLayout ll_content;
    @ViewInject(R.id.layout_input)
    View layout_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getCommentList(true);
    }

    private void init() throws ParseException {
        x.view().inject(this);
        new HeaderHolder().init(this, "吐槽详情");
        addHeaderView();
        layout_input.setVisibility(View.VISIBLE);
        iv_emotion.setOnClickListener(this);
        iv_emotion_pressed.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        registerBroadCast();
        initList();
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getCommentList(false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getCommentList(true);
            }
        });

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

        reportTypeSelectDialog = Windows.createSelectReportTypeDialog(this, this);

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(edt_content);

    }

    private void initList() {
        commentDatas = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentDatas);
        lv_comment.setAdapter(commentAdapter);
        lv_comment.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - 1 >= totalNum) {
                    smartRefreshLayout.setLoadmoreFinished(true);
                }
            }
        });
        lv_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                createSelectFunDialog(commentDatas.get(position - 1));
                funSelectDialog.show();
                tempComment = commentDatas.get(position - 1);
            }
        });


    }

    private CommonDialog funSelectDialog;

    public TextView tv_reply;
    public TextView tv_report;
    public TextView tv_delete;
    public TextView tv_reply_anonymous;


    /**
     * commentFlag :0 点击评论中的评论按钮 1：点击内容中的评论按钮
     */
    private void createSelectFunDialog(Comment comment) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_function_comment, null);
        tv_reply = (TextView) view.findViewById(R.id.tv_reply);
        tv_report = (TextView) view.findViewById(R.id.tv_report);
        tv_delete = (TextView) view.findViewById(R.id.tv_delete);
        tv_reply_anonymous = (TextView) view.findViewById(R.id.tv_reply_anonymous);


        if (App.USER_ID.equals(comment.getUserId())) {
            tv_delete.setVisibility(View.VISIBLE);
            tv_delete.setOnClickListener(this);
            tv_reply.setOnClickListener(this);
            tv_reply.setVisibility(View.GONE);
            tv_reply_anonymous.setVisibility(View.GONE);
            view.findViewById(R.id.divider_reply).setVisibility(View.GONE);
        } else {
            tv_delete.setVisibility(View.GONE);
            tv_reply.setVisibility(View.VISIBLE);
            tv_reply_anonymous.setVisibility(View.VISIBLE);
            view.findViewById(R.id.divider_reply).setVisibility(View.VISIBLE);
        }

        //判断是否是管理员帐号
        if ((App.USER_TYPE == 1) || App.USER_ID.equals(comment.getUserId())) {
            tv_delete.setVisibility(View.VISIBLE);
            tv_delete.setOnClickListener(this);
        } else {
            tv_delete.setVisibility(View.GONE);
        }

        tv_reply.setOnClickListener(this);
        tv_reply_anonymous.setOnClickListener(this);
        tv_report.setOnClickListener(this);
        funSelectDialog = new CommonDialog(this, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    private CommonDialog reportTypeSelectDialog;

    //用于删除、举报、回复等功能选择
    Comment tempComment;


    int pageNum = 1;
    int totalNum = 10;

    //commentId deleteComment
    private void addHeaderView() throws ParseException {
        headerView = LayoutInflater.from(this).inflate(R.layout.tc_content_header_detail, lv_comment, false);
        // headerView.findViewById(R.id.view_divider).setVisibility(View.VISIBLE);
        lv_comment.addHeaderView(headerView);

        holder = new TcContentItemHolder();
        x.view().inject(holder, headerView);
        initHeaderData();
        holder.setOnCommentClickListener(new TcContentItemHolder.OnCommentClickListener() {
            @Override
            public void onCommentClick() {
                commentClick();
            }
        });
        holder.setOnDeleteClickListener(new TcContentItemHolder.OnDeleteClickListener() {
            @Override
            public void onDeleteClick() {
                finish();
            }
        });
    }

    private void initHeaderData() throws ParseException {
        content = (TCContent) getIntent().getSerializableExtra("content");
        if (content != null) {
            holder.bindView(content, this);
            contentId = content.getContentId();
        } else {
            contentId = getIntent().getStringExtra("contentId");
            x.http().get(TcData.getTcDetailById(contentId), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    Type gsonType = new TypeToken<ResponseResult<TCContent>>() {
                    }.getType();
                    ResponseResult<TCContent> response = gson.fromJson(result, gsonType);
                    if (response.getCode() == Code.SUCCESS) {
                        TCContent tcContent = response.getData();
                        try {
                            holder.bindView(tcContent, TcContentDetailActivity.this);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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


    private void getCommentList(final boolean refresh) {
//        final ProgressDialog progressDialog = Windows.loading(this);
        pageNum = refresh ? 1 : pageNum;
        final RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getTcContentCommentList");
        params.addQueryStringParameter("contentId", contentId);
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addParameter("pageNum", pageNum);
        x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Gson gson = new Gson();
                        Type jsonType = new TypeToken<ResponseResult<List<Comment>>>() {
                        }.getType();
                        ResponseResult<List<Comment>> responseResult = gson.fromJson(result, jsonType);
                        if (responseResult.getCode() == Code.SUCCESS) {
                            List<Comment> commentList = responseResult.getData();
                            if (refresh) {
                                commentDatas.clear();
                                smartRefreshLayout.setLoadmoreFinished(false);
                                smartRefreshLayout.finishRefresh();
                                //如果当前是第一页，则获取下一页数据
                                if (pageNum == 1)
                                    pageNum++;
                            } else {
                                pageNum++;
                                smartRefreshLayout.finishLoadmore();
                            }
                            commentDatas.addAll(commentList);
                            commentAdapter.notifyDataSetChanged();
                            try {
                                totalNum = Integer.parseInt(responseResult.getMessage());
                                Log.i("TcDetail--->totalNum:", totalNum + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.toast(TcContentDetailActivity.this, Constant.NETWORK_ERROR);
                        if (refresh) {
                            smartRefreshLayout.finishRefresh();
                        } else {
                            smartRefreshLayout.finishLoadmore(0);
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
//                        progressDialog.dismiss();
                    }
                }

        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (checkInfo()) {
                    if (StringUtil.isEmpty(toUserId))
                        createDialog();
                    else
                        sendComment();
                }

                break;
            case R.id.tv_right:
                break;
            case R.id.iv_comment:
                commentClick();
                break;
            case R.id.iv_emotion:
                emojiPopup.toggle();
                iv_emotion_pressed.setVisibility(View.VISIBLE);
                iv_emotion.setVisibility(View.GONE);
                break;
            case R.id.edt_content:
                emojiPopup.dismiss();
                iv_emotion_pressed.setVisibility(View.GONE);
                iv_emotion.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_emotion_pressed:
                emojiPopup.dismiss();
                iv_emotion_pressed.setVisibility(View.GONE);
                iv_emotion.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_reply:
                anonymous = 0;
                setReplyInfo(tempComment, false);
                funSelectDialog.dismiss();
                break;
            case R.id.tv_reply_anonymous:
                anonymous = 1;
                setReplyInfo(tempComment, true);
                funSelectDialog.dismiss();
                break;
            case R.id.tv_report:
                reportTypeSelectDialog.show();
                funSelectDialog.dismiss();
                break;
            case R.id.tv_delete:
                TcData.deleteComment(this, tempComment.getCommentId(), contentId);
                commentDatas.remove(tempComment);
                commentAdapter.notifyDataSetChanged();
                SquareFragment.sendBroadCast(TcContentItemHolder.DELETE_COMMENT, TcContentDetailActivity.this, contentId);
                funSelectDialog.dismiss();
                content.setCommentCount(content.getCommentCount()- 1);
                holder.setCommentCount(content.getCommentCount());
                break;
            case R.id.tv_divulge_privacy:
            case R.id.tv_personal_attack:
            case R.id.tv_obscenity:
            case R.id.tv_adv:
            case R.id.tv_false_information:
            case R.id.tv_llegal_information:
            case R.id.tv_other:
                reportTypeSelectDialog.dismiss();
                Report.report(this, v, tempComment.getCommentId(), content.getContentId());
                break;
        }

    }

    CommonDialog dialog;

    private void createDialog() {
        dialog = Windows.createPromtDialog(this, "提示", "请问需要匿名发送吗？", "是", "否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (v.getId()) {
                    case R.id.tv_left:
                        anonymous = 0;
                        dialog.dismiss();
                        break;
                    case R.id.tv_right:
                        anonymous = 1;
                        break;
                }
                sendComment();
            }
        });
        dialog.show();
    }

    private void commentClick() {
        edt_content.setHint("说点什么吧");
        edt_content.setText("");
        CommonUtil.manageInputMethod(this);
        emojiPopup.dismiss();
        toUserId = "";
        toNickName = "";
        toCommentContent = "";
        toCommentId = "";
    }

    String toUserId;
    String toCommentContent;
    String toCommentId;
    String toNickName;
    int anonymous;

    private boolean checkInfo() {
        strContent = edt_content.getText().toString();
        if (StringUtil.isEmpty(strContent)) {
            CommonUtil.toast(this, "内容不能为空噢！");
            return false;
        }
        return true;

    }


    private void sendComment() {
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = TcData.sendComment(contentId,strContent,toUserId,toCommentContent,toCommentId,toNickName,anonymous);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult responseResult = gson.fromJson(result, ResponseResult.class);
                if (responseResult.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(TcContentDetailActivity.this, "评论成功");
                    getCommentList(true);
                    edt_content.setText("");
                    content.setCommentCount(content.getCommentCount() + 1);
                    holder.setCommentCount(content.getCommentCount());
                    SquareFragment.sendBroadCast(TcContentItemHolder.COMMENT, TcContentDetailActivity.this, contentId);
                    CommonUtil.hideKeyBoard(TcContentDetailActivity.this, edt_content.getWindowToken());
                    emojiPopup.dismiss();
                    iv_emotion.setVisibility(View.VISIBLE);
                    iv_emotion_pressed.setVisibility(View.GONE);
                } else if (responseResult.getCode() == Code.PROHIBIT) {
                    CommonUtil.toast(TcContentDetailActivity.this, "您已被禁言");
                }else if (responseResult.getCode() == Code.ILLEGAL_CONTENT) {
                    CommonUtil.toast(TcContentDetailActivity.this, "您发布的信息包含敏感词汇，禁止发送");
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
                progressDialog.dismiss();
            }
        });
    }

    CommentBroadCastReceiver commentBroadCastReceiver;

    //register comment clicked broadcast to update ui
    // sendBroadcast:commentAdapter
    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("commentClick");
        commentBroadCastReceiver = new CommentBroadCastReceiver();
        registerReceiver(commentBroadCastReceiver, intentFilter);
    }

    public static void sendBroadCast(Context context, Comment comment) {
        Intent intent = new Intent();
        intent.setAction("commentClick");
        intent.putExtra("comment", comment);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(commentBroadCastReceiver);
    }

    //用于回复
    Comment comment;

    //点击评论图标时，对指定用户评论,设置指定用户评论信息
    class CommentBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            comment = (Comment) intent.getSerializableExtra("comment");
            setReplyInfo(comment, false);
        }
    }

    private void setReplyInfo(Comment comment, boolean anonymous) {
        emojiPopup.dismiss();
        iv_emotion_pressed.setVisibility(View.GONE);
        iv_emotion.setVisibility(View.VISIBLE);
        String name = "匿名";
        if (comment.getAnonymous() == 0) {
            name = comment.getNickName();
            toNickName = comment.getNickName();
        } else {
            //如果回复匿名评论的评论，则将被评论的昵称设置为匿名
            toNickName = "匿名";
        }
        if (!anonymous) {
            edt_content.setHint("回复 " + name);
        } else {
            edt_content.setHint("匿名回复 " + name);
        }
        edt_content.setText("");
        edt_content.requestFocus();
        toUserId = comment.getUserId();
        toCommentContent = comment.getCommentContent();
        toCommentId = comment.getCommentId();
        CommonUtil.manageInputMethod(TcContentDetailActivity.this);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (CommonUtil.isShouldHideKeyboard(v, ev)) {
                CommonUtil.hideKeyBoard(TcContentDetailActivity.this, v.getWindowToken());
                emojiPopup.dismiss();
                iv_emotion.setVisibility(View.VISIBLE);
                iv_emotion_pressed.setVisibility(View.GONE);
                if (StringUtil.isEmpty(edt_content.getText().toString())) {
                    edt_content.setHint("说点什么吧");
                    toUserId = "";
                    toNickName = "";
                    toCommentContent = "";
                    toCommentId = "";
                }
                comment = null;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


//    /**
//     * 初始化表情面板
//     */
//    public void initEmotionMainFragment() {
//        //构建传递参数
//        Bundle bundle = new Bundle();
//        //绑定主内容编辑框
//        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT, true);
//        //隐藏控件
//        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN, false);
//        //替换fragment
//        //创建修改实例
//        emotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class, bundle);
//        emotionMainFragment.bindToContentView(ll_content);
//        emotionMainFragment.setOnSendBtnListener(new EmotionMainFragment.OnSendBtnListener() {
//            @Override
//            public void onClick() {
//                checkInfo();
//                sendComment();
//            }
//        });
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        // Replace whatever is in thefragment_container view with this fragment,
//        // and add the transaction to the backstack
//        transaction.replace(R.id.fl_emotionview_main, emotionMainFragment);
//        transaction.addToBackStack(null);
//        //提交修改
//        transaction.commit();
//    }

//    @Override
//    public void onBackPressed() {
//        /**
//         * 判断是否拦截返回键操作
//         */
//        if (!emotionMainFragment.isInterceptBackPress()) {
//            finish();
//        }
//    }


}
