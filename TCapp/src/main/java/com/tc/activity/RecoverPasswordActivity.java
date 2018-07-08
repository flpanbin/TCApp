package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.tc.bean.Code;
import com.tc.bean.Require;
import com.tc.bean.ResponseResult;
import com.tc.data.Constant;
import com.tc.data.UserData;
import com.tc.utils.CommonUtil;
import com.tc.utils.StringUtil;
import com.tc.view.HeaderHolder;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.smssdk.SMSSDK;

import static com.example.pb.myapplication.R.id.edt_nick_name;

/**
 * Created by PB on 2018/1/28.
 */

@ContentView(R.layout.activity_recover_password)
public class RecoverPasswordActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.edt_phone)
    EditText edt_phone;
    @ViewInject(R.id.edt_password)
    EditText edt_password;
    @ViewInject(R.id.btn_get_code)
    Button btn_get_code;
    @ViewInject(R.id.edt_code)
    EditText edt_code;
    @ViewInject(R.id.btn_ok)
    Button btn_ok;
    private String strPhone;
    private String strPwd;
    private String strCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);

        init();
    }

    private void init() {
        x.view().inject(this);
        new HeaderHolder().init(this, "找回密码").hideRightText();
        btn_get_code.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
                getVCode();
                break;
            case R.id.btn_ok:
                if (checkInfo())
                    recoverPassword();
                break;
        }
    }

    private boolean checkInfo() {
        strPhone = edt_phone.getText().toString();
        strPwd = edt_password.getText().toString();
        strCode = edt_code.getText().toString();

        if (!StringUtil.paramNull(this, new Require().put(strPhone, "请输入手机号码").put(strPwd, "请输入密码").put(strCode, "请输入验证码")))
            return false;
        if (!CommonUtil.Patterns.MOBILE.matcher(strPhone).matches()) {
            CommonUtil.toast(this, "手机号不正确");
        } else if (strPwd.length() < 6) {
            CommonUtil.toast(this, "密码不能少于六位");
        } else
            return true;

        return false;

    }

    /**
     * 获取验证码
     */
    private void getVCode() {
        strPhone = edt_phone.getText().toString();
        if (StringUtil.isEmpty(strPhone)) {
            CommonUtil.toast(this, "请输入手机号码");
            return;
        } else if (!CommonUtil.Patterns.MOBILE.matcher(strPhone).matches()) {
            CommonUtil.toast(this, "手机号不正确");
            return;
        }
        SMSSDK.getVerificationCode("+86", strPhone);
        new Thread(new VCodeThread()).start();
    }

    int resendTime = 60;
    Handler vCodeHandler = new Handler();

    class VCodeThread implements Runnable {

        @Override
        public void run() {
            while (resendTime > 0) {
                vCodeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_get_code.setText(resendTime + "秒后重试");
                        btn_get_code.setEnabled(false);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resendTime--;
            }
            vCodeHandler.post(new Runnable() {
                @Override
                public void run() {
                    btn_get_code.setText("获取验证码");
                    btn_get_code.setEnabled(true);
                }
            });
            resendTime = 60;
        }
    }

    private void recoverPassword() {
        final ProgressDialog progressDialog = Windows.loading(this);
        x.http().get(UserData.recoverPassword(strPhone, strPwd, strCode), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(RecoverPasswordActivity.this, "修改成功");
                    startActivity(new Intent(RecoverPasswordActivity.this, LoginActivity.class));
                } else if (response.getCode() == Code.ACCOUNT_NOT_EXISTS) {
                    CommonUtil.toast(RecoverPasswordActivity.this, "该帐号不存在");
                } else if (response.getCode() == Code.CODE_ERROR) {
                    CommonUtil.toast(RecoverPasswordActivity.this, "验证码错误");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(RecoverPasswordActivity.this, Constant.NETWORK_ERROR);
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
}
