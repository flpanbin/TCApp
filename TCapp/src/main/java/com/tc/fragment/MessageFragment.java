package com.tc.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pb.myapplication.MainActivity;
import com.example.pb.myapplication.R;
import com.tc.activity.MessageActivity;
import com.tc.activity.ReceivedCommentActivity;
import com.tc.activity.ReceivedLikeActivity;
import com.tc.data.Constant;
import com.tc.view.ScrollingTrickListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Field;

/**
 * Created by PB on 2017/7/25.
 */
@ContentView(R.layout.fragment_message)
public class MessageFragment extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.ll_comment)
    LinearLayout ll_comment;
    @ViewInject(R.id.ll_like)
    LinearLayout ll_like;
    @ViewInject(R.id.ll_notify)
    LinearLayout ll_notify;
    @ViewInject(R.id.iv_notification_comment)
    ImageView iv_notification_comment;
    @ViewInject(R.id.iv_notification_like)
    ImageView iv_notification_like;
    @ViewInject(R.id.iv_notification_system)
    ImageView iv_notification_system;
    Activity activity;

    @ViewInject(R.id.iv_comment)
    ImageView iv_comment;
    @ViewInject(R.id.iv_up)
    ImageView iv_up;
    @ViewInject(R.id.iv_system_message)
    ImageView iv_system_message;

    @ViewInject(R.id.tv_dot_system)
    TextView tv_dot_system;
    @ViewInject(R.id.tv_dot_up)
    TextView tv_dot_up;

    ReceivedCommentFragment receivedCommentFragment;
    ReceivedLikeFragment receivedLikeFragment;
    SystemMsgFragment systemMsgFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        activity = getActivity();
        init();
        return view;
    }

    private void init() {
        ll_comment.setOnClickListener(this);
        ll_like.setOnClickListener(this);
        ll_notify.setOnClickListener(this);

        initMsgNotifyState();
        setDefaultFragment();
        registerMsgReceiver();
    }

    private void setDefaultFragment() {
        FragmentManager manager = this.getChildFragmentManager();
        receivedCommentFragment = new ReceivedCommentFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layout_container, receivedCommentFragment);
        transaction.commit();
        resetTabView();
        iv_comment.setImageResource(R.drawable.icon_big_comment_selected);
    }


//    private void initView() {
//
//        if (Constant.new_comment) {
//            iv_notification_comment.setVisibility(View.VISIBLE);
//        } else {
//            iv_notification_comment.setVisibility(View.GONE);
//        }
//
//        if (Constant.new_like) {
//            iv_notification_like.setVisibility(View.VISIBLE);
//        } else {
//            iv_notification_like.setVisibility(View.GONE);
//        }
//
//        if (Constant.new_system_message) {
//            iv_notification_system.setVisibility(View.VISIBLE);
//        } else {
//            iv_notification_system.setVisibility(View.GONE);
//        }
//
//    }


    @Override
    public void onClick(View v) {
        Intent intent = null;

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (v.getId()) {
            case R.id.ll_comment:
//                intent = new Intent(activity, ReceivedCommentActivity.class);
//                iv_notification_comment.setVisibility(View.GONE);

                resetTabView();
                iv_comment.setImageResource(R.drawable.icon_big_comment_selected);
                if (receivedCommentFragment == null) {
                    receivedCommentFragment = new ReceivedCommentFragment();
                }
                transaction.replace(R.id.layout_container, receivedCommentFragment);

                Constant.new_comment = false;
                break;
            case R.id.ll_like:
//                intent = new Intent(activity, ReceivedLikeActivity.class);
//                iv_notification_like.setVisibility(View.GONE);
                resetTabView();
                iv_up.setImageResource(R.drawable.icon_big_up_selected);
                if (receivedLikeFragment == null) {
                    receivedLikeFragment = new ReceivedLikeFragment();
                }
                transaction.replace(R.id.layout_container, receivedLikeFragment);

                Constant.new_like = false;
                tv_dot_up.setVisibility(View.GONE);
                break;
            case R.id.ll_notify:
                //   intent = new Intent(activity, MessageActivity.class);
                //    iv_notification_system.setVisibility(View.GONE);
                resetTabView();
                iv_system_message.setImageResource(R.drawable.icon_big_system_message_selected);
                if (systemMsgFragment == null) {
                    systemMsgFragment = new SystemMsgFragment();
                }
                transaction.replace(R.id.layout_container, systemMsgFragment);
                Constant.new_system_message = false;
                tv_dot_system.setVisibility(View.GONE);
                break;

        }
        transaction.commit();

    }

    private void resetTabView() {
        iv_comment.setImageResource(R.drawable.icon_big_comment);
        iv_up.setImageResource(R.drawable.icon_big_up);
        iv_system_message.setImageResource(R.drawable.icon_big_system_message);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Field childFragmentManager = null;
        try {
            childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        childFragmentManager.setAccessible(true);
        try {
            childFragmentManager.set(this, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initMsgNotifyState() {
        if (Constant.new_system_message)
            tv_dot_system.setVisibility(View.VISIBLE);
        else {
            tv_dot_system.setVisibility(View.GONE);
        }
        if (Constant.new_like)
            tv_dot_up.setVisibility(View.VISIBLE);
        else {
            tv_dot_up.setVisibility(View.GONE);
        }
    }

    MsgNotifyReceiver msgNotifyReceiver;

    private void registerMsgReceiver() {
        msgNotifyReceiver = new MsgNotifyReceiver();
        IntentFilter filter = new IntentFilter(Constant.MSG_NOTIFY);
        activity.registerReceiver(msgNotifyReceiver, filter);
    }

    //通知消息提醒 显示红点点...
    ///type:0:评论消息已读，1：点赞消息已读，2:系统消息已读 3：新的评论消息，4：新的点赞消息,5：新的系统消息
    class MsgNotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constant.MSG_NOTIFY_TYPE, 0);
            if (type == 1) {
                tv_dot_up.setVisibility(View.GONE);
            } else if (type == 2) {
                tv_dot_system.setVisibility(View.GONE);
            } else if (type == 4) {
                tv_dot_up.setVisibility(View.VISIBLE);
            } else if (type == 5) {
                tv_dot_system.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        activity.unregisterReceiver(msgNotifyReceiver);
        super.onDestroy();
    }
}
