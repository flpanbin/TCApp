package com.tc.activity;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.utils.WebUtil;

import static com.tc.conf.Config.SERVER_HOST;

@ContentView(R.layout.activity_web)
public class WebActivity extends BaseActivity {

    @ViewInject(R.id.webview)
    private WebView webView;
    private String title;

    private String url;

    @ViewInject(R.id.iv_close)
    private ImageView iv_close;
    @ViewInject(R.id.iv_back)
    private ImageView iv_back;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);

        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");

        initView();
        initData();

    }

    private void initData() {
        webView.loadUrl(url);
    }

    private void initView() {

        tv_title.setText(title);
        iv_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });

        WebUtil.setWebSetting(webView);
        //使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
               view.loadUrl(url);
                return true;
            };
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ViewGroup) webView.getParent()).removeView(webView);
        webView.removeAllViews();
        webView.destroy();
    }

    public static Intent getTargetIntent(Context ctx, String title, String url) {
        Intent intent = new Intent(ctx, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        return intent;
    }
}
