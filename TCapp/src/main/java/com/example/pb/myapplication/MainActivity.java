package com.example.pb.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.tc.activity.BaseActivity;
import com.tc.activity.LoginActivity;
import com.tc.activity.TempActivity;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.UserPreference;
import com.tc.fragment.ActivityFragment;
import com.tc.fragment.MeFragment;
import com.tc.fragment.MessageFragment;
import com.tc.fragment.SquareFragment;
import com.tc.service.DemoPushService;
import com.tc.service.IntentService;
import com.tc.utils.ActivityCollector;
import com.tc.utils.CommonUtil;
import com.tc.data.Constant;
import com.tc.utils.SPUtil;
import com.tc.utils.StringUtil;
import com.tc.view.statusbar.StatusBarBackground;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;
    @ViewInject(R.id.ll_square)
    LinearLayout ll_square;
    @ViewInject(R.id.ll_activity)
    LinearLayout ll_activity;
    @ViewInject(R.id.ll_msg)
    LinearLayout ll_msg;
    @ViewInject(R.id.ll_me)
    LinearLayout ll_me;
    List<Fragment> fragmentList;
    @ViewInject(R.id.iv_square)
    ImageView iv_square;
    @ViewInject(R.id.iv_activity)
    ImageView iv_activity;
    @ViewInject(R.id.iv_me)
    ImageView iv_me;
    @ViewInject(R.id.iv_message)
    ImageView iv_msg;
    @ViewInject(R.id.tv_square)
    TextView tv_square;
    @ViewInject(R.id.tv_message)
    TextView tv_msg;
    @ViewInject(R.id.tv_activity)
    TextView tv_activity;
    @ViewInject(R.id.tv_me)
    TextView tv_me;
    @ViewInject(R.id.iv_release)
    ImageView iv_release;
    FragmentPagerAdapter mPageAdater;

    @ViewInject(R.id.tv_dot_message)
    TextView tv_dot_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), IntentService.class
        );
        setStatusBarColor(R.color.main_color);
        CommonUtil.initStatusBar(this);
        x.view().inject(this);
        checkAutoLogin();
        init();

        // PushManager.getInstance().bindAlias(this, App.USER_ID);
    }


    private void init() {

        fragmentList = new ArrayList<>();
        Fragment squareFragment = new SquareFragment();
        Fragment activityFragment = new ActivityFragment();
        Fragment meFragment = new MeFragment();
        Fragment msgFragment = new MessageFragment();
        fragmentList.add(squareFragment);
        fragmentList.add(activityFragment);
        fragmentList.add(msgFragment);
        fragmentList.add(meFragment);


        mPageAdater = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        viewPager.setAdapter(mPageAdater);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelect(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ll_activity.setOnClickListener(this);
        ll_square.setOnClickListener(this);
        ll_me.setOnClickListener(this);
        ll_msg.setOnClickListener(this);
        iv_release.setOnClickListener(this);

        setSelect(0);

        registerMsgReceiver();
    }

    private void resetTab() {
        iv_square.setImageResource(R.drawable.icon_square);
        iv_activity.setImageResource(R.drawable.icon_find);
        iv_me.setImageResource(R.drawable.icon_me);
        iv_msg.setImageResource(R.drawable.icon_message);
        tv_activity.setTextColor(getResources().getColor(R.color.gray_bottem_tab_tv));
        tv_square.setTextColor(getResources().getColor(R.color.gray_bottem_tab_tv));
        tv_me.setTextColor(getResources().getColor(R.color.gray_bottem_tab_tv));
        tv_msg.setTextColor(getResources().getColor(R.color.gray_bottem_tab_tv));
    }

    private void setTab(int i) {
        resetTab();
        switch (i) {
            case 0:
                iv_square.setImageResource(R.drawable.icon_square_selected);
                tv_square.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case 1:
                iv_activity.setImageResource(R.drawable.icon_find_selected);
                tv_activity.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case 2:
                iv_msg.setImageResource(R.drawable.icon_message_selected);
                tv_msg.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case 3:
                iv_me.setImageResource(R.drawable.icon_me_selected);
                tv_me.setTextColor(getResources().getColor(R.color.main_color));
                break;
        }
    }

    private void setSelect(int i) {
        setTab(i);
        viewPager.setCurrentItem(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_square:
                setSelect(0);
                break;
            case R.id.ll_activity:
                setSelect(1);
                break;
            case R.id.ll_msg:
                setSelect(2);
                tv_dot_msg.setVisibility(View.GONE);
                break;
            case R.id.ll_me:
                setSelect(3);
                break;
            case R.id.iv_release:
                rotate();
                break;
        }
    }

    private void rotate() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.release_image_rotate);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(!animation.getFillAfter());
        iv_release.startAnimation(animation);
    }


    /**
     * 检查是否自动登录
     *
     * @return
     */
    public void checkAutoLogin() {
        String token = (String) SPUtil.get(this, UserPreference.USER_TOKEN, "");
        if (StringUtil.isEmpty(token)) {
            turnToLogin();
            return;
        }
        App.ACCESS_TOKEN = token;
        App.USER_ID = (String) SPUtil.get(this, UserPreference.USER_ID, "");
        App.USER_TYPE = (int) SPUtil.get(this, UserPreference.USER_TYPE, 0);
        checkToken(token);

    }

    /**
     * 跳转到登录界面
     */
    private void turnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 检查token是否有效，如果失效则重新登录
     *
     * @param token
     */
    private void checkToken(final String token) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/checkAutoLogin");
        params.addQueryStringParameter("token", token);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() != Code.SUCCESS) {
                    turnToLogin();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(MainActivity.this, Constant.NETWORK_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    //用于处理ReceivedCommentFragment中 触摸输入框以外的区域的点击事件
    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent ev);
    }

    MyTouchListener myTouchListener;
    ArrayList<MyTouchListener> touchListeners = new ArrayList<>(2);

    public void registerMyOnTouchListener(MyTouchListener myTouchListener) {
        touchListeners.add(myTouchListener);
    }

    public void unRegisterMyOnTouchListener(MyTouchListener myTouchListener) {
        touchListeners.remove(myTouchListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener touchListener : touchListeners)
            touchListener.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    MsgNotifyReceiver msgNotifyReceiver;


    private void registerMsgReceiver() {
        msgNotifyReceiver = new MsgNotifyReceiver();
        IntentFilter filter = new IntentFilter(Constant.MSG_NOTIFY);
        registerReceiver(msgNotifyReceiver, filter);
    }

    //通知消息提醒 显示红点点...
    //type:0，1，2 取消显示红点 3，4，5：显示红点
    class MsgNotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constant.MSG_NOTIFY_TYPE, 0);
            if (type < 3) {
                tv_dot_msg.setVisibility(View.GONE);
            } else {
                tv_dot_msg.setVisibility(View.VISIBLE);
            }
        }
    }

    long firstPressed = 0;

    @Override
    public void onBackPressed() {
        if (firstPressed == 0) {
            firstPressed = System.currentTimeMillis();
            CommonUtil.toast(this, "再按一次退出程序");
            return;
        } else {
            long secondPressed = System.currentTimeMillis();
            if ((secondPressed - firstPressed) / 1000 < 2) {
                ActivityCollector.finishAll();
            } else {
                CommonUtil.toast(this, "再按一次退出程序");
                firstPressed = System.currentTimeMillis();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgNotifyReceiver);
        super.onDestroy();
    }
}
