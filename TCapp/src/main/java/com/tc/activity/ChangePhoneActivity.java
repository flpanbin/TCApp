package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.utils.CommonUtil;
import com.tc.utils.StringUtil;
import com.tc.view.HeaderHolder;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.smssdk.SMSSDK;

@ContentView(R.layout.activity_change_phone)
public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.edt_change_phone)
    private EditText edt_change_phone;
    @ViewInject(R.id.edt_get_code)
    private EditText edt_get_code;
    @ViewInject(R.id.btn_get_phone_code)
    private Button btn_get_phone_code;
    @ViewInject(R.id.btn_ok)
    private Button btn_agree;

    private String strPhone;
    private String strCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {
        btn_get_phone_code.setOnClickListener(this);
        btn_agree.setOnClickListener(this);
        new HeaderHolder().init(this, "更改手机号").hideRightText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_phone_code:
                getPhoneCode();
                break;
            case R.id.btn_ok:
                if (checkInfo())
                    agree();
                break;
        }
    }

    private Boolean checkInfo() {
        strPhone = edt_change_phone.getText().toString();
        strCode = edt_get_code.getText().toString();
        if (strCode.isEmpty()) {
            CommonUtil.toast(this, "验证码不能为空");
            return false;
        }
        if (StringUtil.isEmpty(strPhone)) {
            CommonUtil.toast(this, "请输入手机号码");
            return false;
        }
        if (!CommonUtil.Patterns.MOBILE.matcher(strPhone).matches()) {
            CommonUtil.toast(this, "手机号不正确");
            return false;
        }
        return true;

    }

    private void getPhoneCode() {
        strPhone = edt_change_phone.getText().toString();
        if (strPhone.isEmpty()) {
            CommonUtil.toast(this, "手机号为空");
            return;
        }
        if (!CommonUtil.Patterns.MOBILE.matcher(strPhone).matches()) {
            CommonUtil.toast(this, "手机号不正确");
            return;
        }
        SMSSDK.getVerificationCode("+86", strPhone);
        new Thread(new ChangeThread()).start();
    }

    Handler handler = new Handler();
    int time = 60;

    private class ChangeThread implements Runnable {
        @Override
        public void run() {
            while (time > 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_get_phone_code.setText(time + "秒后重试");
                        btn_get_phone_code.setEnabled(false);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time--;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btn_get_phone_code.setText("获取验证码");
                    btn_get_phone_code.setEnabled(true);
                }
            });
            time = 60;
        }
    }

    private void agree() {
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/changePhone");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("username", strPhone);
        params.addQueryStringParameter("code", strCode);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(ChangePhoneActivity.this, "更换手机号码成功!");
                    Intent intent = new Intent(ChangePhoneActivity.this, SetUpActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.getCode() == Code.CODE_ERROR) {
                    CommonUtil.toast(ChangePhoneActivity.this, "验证码错误!");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //要在activity销毁时反注册，否侧会造成内存泄漏问题
        SMSSDK.unregisterAllEventHandler();
    }

}
