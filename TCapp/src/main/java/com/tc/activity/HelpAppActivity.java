package com.tc.activity;


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
import com.tc.view.HeaderHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@ContentView(R.layout.activity_help_app)
public class HelpAppActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.btn_submit)
    private Button btn_submit;
    @ViewInject(R.id.edt_submit)
    private EditText edt_submit;

    private String submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }
    private void init(){
        new HeaderHolder().init(this,"帮助与反馈").hideRightText();
        btn_submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        submit=edt_submit.getText().toString();
        if(!submit.isEmpty()){
            try {
                submitInformation();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else
            CommonUtil.toast(this,"内容为空，不能提交");

    }
    private void submitInformation() throws UnsupportedEncodingException {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/feedBack");
        params.addQueryStringParameter("content", submit);
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(HelpAppActivity.this, "提交成功");
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

            }
        });
    }
}
