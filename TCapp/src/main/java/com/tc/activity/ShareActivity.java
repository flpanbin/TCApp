package com.tc.activity;

import android.os.Bundle;

import com.example.pb.myapplication.R;
import com.tc.view.HeaderHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by PB on 2018/3/16.
 */
@ContentView(R.layout.activity_share)
public class ShareActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        new HeaderHolder().init(this, "分享").hideRightText();
    }
}
