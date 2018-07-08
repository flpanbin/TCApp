package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.pb.myapplication.R;
import com.tc.data.UserPreference;
import com.tc.utils.ActivityCollector;
import com.tc.utils.CommonUtil;
import com.tc.utils.SPUtil;
import com.tc.view.CommonDialog;
import com.tc.view.HeaderHolder;
import com.tc.view.Windows;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.tc.conf.Config.SERVER_HOST;

@ContentView(R.layout.activity_set_up)
public class SetUpActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.rl_login_out)
    RelativeLayout rl_login_out;
    @ViewInject(R.id.rl_change_phone)
    private RelativeLayout rl_change_phone;
    @ViewInject(R.id.rl_change_code)
    private RelativeLayout rl_change_code;
    @ViewInject(R.id.rl_about)
    private RelativeLayout rl_about;
    //    @ViewInject(R.id.rl_app_information)
//    private RelativeLayout rl_app_information;
    @ViewInject(R.id.rl_help)
    private RelativeLayout rl_help;
    @ViewInject(R.id.rl_clear_cache)
    private RelativeLayout rl_clear_cache;
    @ViewInject(R.id.rl_agreement)
    private RelativeLayout rl_agreement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {
        new HeaderHolder().init(this, "设置").hideRightText();
        rl_login_out.setOnClickListener(this);
        rl_change_phone.setOnClickListener(this);
        rl_change_code.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_help.setOnClickListener(this);
        rl_clear_cache.setOnClickListener(this);
        rl_agreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_login_out:
                loginOut();
                break;
            case R.id.rl_change_phone:
                intent = new Intent(this, ChangePhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_change_code:
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_help:
                intent = new Intent(this, HelpAppActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_about:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.rl_clear_cache:
                clearCache();
                break;
            case R.id.rl_agreement:
                intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "BM使用条款与隐私协议");
                intent.putExtra("url", SERVER_HOST + "html/agreement.html");
                startActivity(intent);
                break;
        }
    }


    private void loginOut() {
        SPUtil.remove(this, UserPreference.USER_TOKEN);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        ActivityCollector.finishAll();
    }

    CommonDialog dialog;

    private void clearCache() {
//        Glide.get(this).clearDiskCache();
        dialog = Windows.createPromtDialog(this, "提示", "确定要删除所有缓存吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_left:
                        dialog.dismiss();
                        break;
                    case R.id.tv_right:
                        dialog.dismiss();
                        Glide.get(SetUpActivity.this).clearMemory();
                        x.image().clearMemCache();
                        x.image().clearCacheFiles();
                        CommonUtil.toast(SetUpActivity.this, "清除成功");
                        break;
                }
            }
        });
        dialog.show();
    }
}