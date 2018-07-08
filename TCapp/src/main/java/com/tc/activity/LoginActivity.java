package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pb.myapplication.MainActivity;
import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.bean.Code;
import com.tc.bean.Require;
import com.tc.data.UserPreference;
import com.tc.model.User;
import com.tc.application.App;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.utils.ActivityCollector;
import com.tc.utils.CommonUtil;
import com.tc.data.Constant;
import com.tc.utils.SPUtil;
import com.tc.utils.StringUtil;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;

/**
 * Created by PB on 2017/6/22.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.tv_register)
    TextView tv_register;
    @ViewInject(R.id.edt_phone)
    EditText edt_phone;
    @ViewInject(R.id.edt_password)
    EditText edt_password;
    @ViewInject(R.id.btn_login)
    Button btn_login;
    @ViewInject(R.id.tv_forget_pwd)
    TextView tv_forget_pwd;

    private String strPhone;
    private String strPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
        setDefaultLoginInfo();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_register:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                if (checkInfo()) {
                    login();
                }
                break;
            case R.id.tv_forget_pwd:
                intent = new Intent(this, RecoverPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/login");
        params.addQueryStringParameter("username", strPhone);
        params.addQueryStringParameter("password", StringUtil.encodePassword(strPwd, "md5"));
        params.addQueryStringParameter("cid", App.CID);
        x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Gson gson = new Gson();
                        Type jsonType = new TypeToken<ResponseResult<User>>() {
                        }.getType();
                        ResponseResult<User> response = gson.fromJson(result, jsonType);
                        if (response.getCode() == Code.SUCCESS) {
                            saveData(response);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            ActivityCollector.finishAll();
                        } else {
                            CommonUtil.toast(LoginActivity.this, "用户名或密码错误");
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                        CommonUtil.toast(LoginActivity.this, Constant.NETWORK_ERROR);
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

    private boolean checkInfo() {
        strPhone = edt_phone.getText().toString();
        strPwd = edt_password.getText().toString();

        if (!StringUtil.paramNull(this, new Require().put(strPhone, "请输入手机号码").put(strPwd, "请输入密码")))
            return false;
        if (!CommonUtil.Patterns.MOBILE.matcher(strPhone).matches()) {
            CommonUtil.toast(this, "手机号不正确");
            return false;
        }
        return true;
    }

    /**
     * 保存数据到sharedpreference
     *
     * @param response
     */
    private void saveData(ResponseResult<User> response) {
        User user = response.getData();
        App.ACCESS_TOKEN = user.getToken();
        App.USER_ID = user.getUserId();
        App.USER_TYPE = user.getUserType();
        UserPreference.updateUserInfo(this, user);
        UserPreference.savePwd(this, strPwd);
        UserPreference.saveToken(this, user.getToken());
    }

    private void setDefaultLoginInfo() {
        strPhone = (String) SPUtil.get(this, UserPreference.USER_NAME, "");
        if (StringUtil.isEmpty(strPhone))
            return;
        strPwd = (String) SPUtil.get(this, UserPreference.USER_PWD, "");
        edt_phone.setText(strPhone);
        edt_password.setText(strPwd);
        edt_phone.setSelection(strPhone.length());
    }
}
