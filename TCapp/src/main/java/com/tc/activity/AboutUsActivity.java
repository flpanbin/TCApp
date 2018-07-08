package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.data.CommonData;
import com.tc.model.Version;
import com.tc.utils.CommonUtil;
import com.tc.utils.SPUtil;
import com.tc.utils.UpdateManager;
import com.tc.view.HeaderHolder;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;

import static com.tc.conf.Config.SERVER_HOST;

/**
 * Created by Administrator on 2018/2/2.
 */
@ContentView(R.layout.activity_about_us)
public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.tv_version_name)
    private TextView tv_version_name;
    @ViewInject(R.id.tv_not_new)
    private TextView tv_not_new;
    @ViewInject(R.id.tv_new)
    private TextView tv_new;
    @ViewInject(R.id.ll_version_check)
    private LinearLayout ll_version_check;
    @ViewInject(R.id.tv_version_info)
    private TextView tv_version_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        init();
    }

    private void init() {
        x.view().inject(this);
        new HeaderHolder().init(this, "关于Big Mouth").hideRightText();
        tv_version_name.setText("BigMouth " + UpdateManager.getInstance().getVersionName(this));
        if (App.latestVersion) {
            tv_not_new.setVisibility(View.GONE);
            tv_new.setVisibility(View.VISIBLE);
        } else {
            tv_not_new.setVisibility(View.VISIBLE);
            tv_new.setVisibility(View.GONE);
        }
        ll_version_check.setOnClickListener(this);
        tv_version_info.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_version_check:
                CommonData.getVersion(this, true, false);
                break;
            case R.id.tv_version_info:
                final ProgressDialog progressDialog = Windows.loading(this);
                x.http().get(CommonData.getVersionInfByVersionCode(AboutUsActivity.this), new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Gson gson = new Gson();
                                Type jsonType = new TypeToken<ResponseResult<Version>>() {
                                }.getType();
                                ResponseResult<Version> response = gson.fromJson(result, jsonType);
                                if (response.getCode() == Code.SUCCESS) {
                                    Version version = response.getData();
                                    Windows.createPromtDialog(AboutUsActivity.this, "版本说明", version.getUpdateContet()).show();
                                } else {
                                    CommonUtil.toast(AboutUsActivity.this, "请求错误，错误代码" + response.getCode());
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
                        }
                );

        }

    }
}
