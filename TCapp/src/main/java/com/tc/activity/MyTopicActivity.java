package com.tc.activity;

import android.os.Bundle;

import com.example.pb.myapplication.R;
import com.tc.view.HeaderHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by PB on 2018/2/27.
 */
@ContentView(R.layout.activity_my_topic)
public class MyTopicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {
        new HeaderHolder().init(this, "我的主题").hideRightText();
    }
}
