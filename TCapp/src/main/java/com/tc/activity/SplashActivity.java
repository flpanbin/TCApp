package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pb.myapplication.MainActivity;
import com.example.pb.myapplication.R;
import com.tc.adapter.SplashPageAdapter;
import com.tc.data.UserPreference;
import com.tc.utils.SPUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

import static com.example.pb.myapplication.R.drawable.splash_01;

/**
 * Created by Administrator on 2018/2/2.
 */
@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @ViewInject(R.id.banner)
    BGABanner bgaBanner;
    List<View> views;
    SplashPageAdapter pageAdapter;
    @ViewInject(R.id.tv_enter)
    TextView tv_enter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);

        init();
    }

    private void init() {
        x.view().inject(this);
        views = new ArrayList<View>();
        initData();
        UserPreference.firstLogin(this, false);
//        pageAdapter = new SplashPageAdapter(this, views);

        bgaBanner.setEnterSkipViewIdAndDelegate(R.id.tv_enter, 0, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void initData() {
        views.add(View.inflate(this, R.layout.view_splash_first, null));
        views.add(View.inflate(this, R.layout.view_splash_second, null));
        views.add(View.inflate(this, R.layout.view_splash_third, null));
        bgaBanner.setData(views);


    }
}
