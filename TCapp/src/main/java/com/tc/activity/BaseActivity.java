package com.tc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.example.pb.myapplication.R;
import com.tc.data.Constant;
import com.tc.data.UserPreference;
import com.tc.utils.ActivityCollector;
import com.tc.utils.CommonUtil;
import com.tc.view.CommonDialog;
import com.tc.view.Windows;
import com.tc.view.statusbar.StatusBarBackground;

/**
 * Created by PB on 2017/6/10.
 */

public class BaseActivity extends AppCompatActivity {
    BaseActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setStatusBarColor(R.color.white);
        CommonUtil.initStatusBar(this);
        ActivityCollector.addActivity(this);
    }

    /**
     * 设置状态栏颜色 默认为透明
     */
    protected void setStatusBarColor(int color) {
        StatusBarBackground statusBarBackground = new StatusBarBackground(this, color);
        statusBarBackground.setStatusBarbackColor();
    }

    ForceOfflineReceiver forceOfflineReceiver;
    @Override
    protected void onResume() {
        super.onResume();
        forceOfflineReceiver = new ForceOfflineReceiver();
        IntentFilter filter = new IntentFilter(Constant.FORCE_OFFLINE);
        registerReceiver(forceOfflineReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (forceOfflineReceiver != null) {
            unregisterReceiver(forceOfflineReceiver);
        }
    }
    //强制下线

    class ForceOfflineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            UserPreference.clearToken(context);
            CommonDialog commonDialog = Windows.createPromtDialog(context, "下线通知", "您的帐号已在其他设备登录，您已被迫下线。", "重新登录", "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_right:
                            ActivityCollector.finishAll();
                            break;
                        case R.id.tv_left:
                            ActivityCollector.finishAll();
                            startActivity(new Intent(context, LoginActivity.class));
                            break;
                    }
                }
            }, false);
            commonDialog.setCancelable(false);
            commonDialog.show();
        }
    }


}
