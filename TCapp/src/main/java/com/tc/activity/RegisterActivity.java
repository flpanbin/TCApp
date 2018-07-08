package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.tc.application.App;
import com.tc.bean.Require;
import com.tc.bean.ResponseResult;
import com.tc.bean.Code;
import com.tc.conf.Config;
import com.tc.utils.CommonUtil;
import com.tc.data.Constant;
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

import static com.tc.conf.Config.SERVER_HOST;

/**
 * Created by PB on 2017/6/25.
 */

@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.edt_phone)
    private EditText edt_phone;
    @ViewInject(R.id.edt_password)
    private EditText edt_password;
    @ViewInject(R.id.edt_code)
    private EditText edt_code;
    @ViewInject(R.id.btn_get_code)
    private Button btn_get_code;
    @ViewInject(R.id.btn_register)
    private Button btn_register;
    @ViewInject(R.id.edt_nick_name)
    private EditText edt_nick_name;
    @ViewInject(R.id.tv_item)
    private TextView tv_item;
    private String strPhone;
    private String strPwd;
    private String strCode;
    private String strNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {

        new HeaderHolder().init(this, "注册").hideRightText();
        btn_get_code.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        tv_item.setOnClickListener(this);
    }

    private boolean checkInfo() {
        strPhone = edt_phone.getText().toString();
        strPwd = edt_password.getText().toString();
        strCode = edt_code.getText().toString();
        strNickName = edt_nick_name.getText().toString();

        if (!StringUtil.paramNull(this, new Require().put(strPhone, "请输入手机号码").put(strNickName, "请输入昵称").put(strPwd, "请输入密码").put(strCode, "请输入验证码")))
            return false;
        if (!CommonUtil.Patterns.MOBILE.matcher(strPhone).matches()) {
            CommonUtil.toast(this, "手机号不正确");
        } else if (strPwd.length() < 6) {
            CommonUtil.toast(this, "密码不少于六位");
        } else
            return true;

        return false;


    }

    @Override
    public void onClick(View v) {
        strPhone = edt_phone.getText().toString().trim();
        strCode = edt_code.getText().toString();
        switch (v.getId()) {
            case R.id.btn_get_code:
                getVCode();
                break;
            case R.id.btn_register:
                if (checkInfo()) {
                    register();
                }
                break;
            case R.id.tv_item:
                Intent intent = new Intent(RegisterActivity.this, WebActivity.class);
                intent.putExtra("title", "BM使用条款与隐私协议");
                intent.putExtra("url", SERVER_HOST + "html/agreement.html");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //要在activity销毁时反注册，否侧会造成内存泄漏问题
        SMSSDK.unregisterAllEventHandler();
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

    private void register() {
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/register");
        params.addQueryStringParameter("username", strPhone);
        params.addQueryStringParameter("password", StringUtil.encodePassword(strPwd, "md5"));
        params.addQueryStringParameter("code", strCode);
        params.addQueryStringParameter("nickname", strNickName);
        params.addQueryStringParameter("cid", App.CID);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(RegisterActivity.this, "注册成功!");
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if (response.getCode() == Code.CODE_ERROR) {
                    CommonUtil.toast(RegisterActivity.this, "验证码错误!");
                } else if (response.getCode() == Code.ACCOUNT_EXISTS) {
                    CommonUtil.toast(RegisterActivity.this, "该帐号已注册!");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(RegisterActivity.this, Constant.NETWORK_ERROR);
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



