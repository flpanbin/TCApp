package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
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

@ContentView(R.layout.activity_change_password)
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener{
    @ViewInject(R.id.edt_old_pwd)
    private EditText edt_old_pwd;
    @ViewInject(R.id.edt_new_pwd)
    private EditText edt_new_pwd;
//    @ViewInject(R.id.edt_new_code_check)
//    private EditText edt_new_code_check;
    @ViewInject(R.id.btn_access)
    private Button btn_access;

    private String oldPwd;
    private String newPwd;
//    private String checkCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }
    private void init(){
        new HeaderHolder().init(this,"修改密码").hideRightText();
        btn_access.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (checkInfo()) {
            saveData();
        }
    }
    private Boolean checkInfo(){
        oldPwd = edt_old_pwd.getText().toString();
        newPwd = edt_new_pwd.getText().toString();
     //   checkCode=edt_new_code_check.getText().toString();

       if(newPwd.isEmpty()| oldPwd.isEmpty()) {
           CommonUtil.toast(this, "密码不能为空");
           return false;
       }if (oldPwd.length() < 6| newPwd.length()<6) {
            CommonUtil.toast(this, "密码不少于六位");
            return false;
        }
        return true;
    }

    private void saveData(){
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/modifyPwd");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("newPassword",StringUtil.encodePassword(newPwd, "md5"));
        params.addQueryStringParameter("oldPassword",StringUtil.encodePassword(oldPwd, "md5"));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(ChangePasswordActivity.this,"修改成功");
                    Intent intent = new Intent(ChangePasswordActivity.this, SetUpActivity.class);
                    startActivity(intent);
                    finish();
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
}

