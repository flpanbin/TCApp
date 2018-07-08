package com.tc.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.activity.MyCommentActivity;
import com.tc.activity.MyCollectActivity;
import com.tc.activity.MyConcernActivity;
import com.tc.activity.MyInformationActivity;
import com.tc.activity.MyTcActivity;
import com.tc.activity.MyTopicActivity;
import com.tc.activity.SetUpActivity;
import com.tc.activity.ShareActivity;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.data.UserPreference;
import com.tc.utils.SPUtil;
import com.tc.utils.StringUtil;
import com.tc.utils.UIUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by PB on 2017/7/25.
 */

@ContentView(R.layout.fragment_me)
public class MeFragment extends Fragment implements View.OnClickListener {
    @ViewInject(R.id.rl_my_information)
    private RelativeLayout rl_my_information;
    @ViewInject(R.id.ll_my_collect)
    private LinearLayout ll_my_collect;

    @ViewInject(R.id.ll_my_tc)
    private LinearLayout ll_my_tc;
    @ViewInject(R.id.ll_my_reply)
    private LinearLayout ll_my_reply;
    @ViewInject(R.id.rl_set_up)
    private RelativeLayout rl_set_up;
    @ViewInject(R.id.iv_avatar)
    private ImageView iv_avatar;
    @ViewInject(R.id.ll_my_zt)
    private LinearLayout ll_my_zt;
    @ViewInject(R.id.rl_share)
    private RelativeLayout rl_share;

    Activity activity;

    @ViewInject(R.id.tv_nick_name)
    private TextView tv_nick_name;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        activity = getActivity();
        init();
        return view;
    }

    private void init() {
        setUserInfo();
        rl_my_information.setOnClickListener(this);
        ll_my_collect.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        ll_my_tc.setOnClickListener(this);
        ll_my_reply.setOnClickListener(this);
        rl_set_up.setOnClickListener(this);
        ll_my_zt.setOnClickListener(this);
        registBroadCast();
    }

    private void setUserInfo() {
        UIUtils.showAvatar(iv_avatar, (String) SPUtil.get(getActivity(), UserPreference.USER_AVATAR, ""));
        tv_nick_name.setText((String) SPUtil.get(activity, UserPreference.USER_NICK_NAME, ""));
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_my_information:
                intent = new Intent(activity, MyInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_my_collect:
                intent = new Intent(activity, MyCollectActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_my_tc:
                intent = new Intent(activity, MyTcActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_my_reply:
                intent = new Intent(activity, MyCommentActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_set_up:
                intent = new Intent(activity, SetUpActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_my_zt:
                intent = new Intent(activity, MyTopicActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_share:
                intent = new Intent(activity, ShareActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showAvatar(String path) {
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(iv_avatar, path, imageOptions);
    }

    UpdateUserInfoBroadCast updateUserInfoBroadCast;

    private void registBroadCast() {
        IntentFilter filter = new IntentFilter("update_userinfo");
        updateUserInfoBroadCast = new UpdateUserInfoBroadCast();
        getContext().registerReceiver(updateUserInfoBroadCast, filter);

    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(updateUserInfoBroadCast);
        super.onDestroy();

    }

    class UpdateUserInfoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setUserInfo();
        }
    }
}
