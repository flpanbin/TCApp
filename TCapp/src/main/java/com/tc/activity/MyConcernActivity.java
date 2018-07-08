package com.tc.activity;

import android.os.Bundle;
import android.view.View;

import com.example.pb.myapplication.R;
import com.tc.view.HeaderHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_my_concern)
public class MyConcernActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        new HeaderHolder().init(this,"我的关注").hideRightText();
    }

    @Override
    public void onClick(View v) {

    }
}
