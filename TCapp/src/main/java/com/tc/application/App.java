package com.tc.application;


import android.content.Context;
import android.support.annotation.NonNull;

import com.example.pb.myapplication.R;
import com.mob.MobApplication;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by PB on 2017/6/11.
 */

public class App extends MobApplication {

    public static String ACCESS_TOKEN;
    public static String USER_ID;
    public static String SCHOOL_ID = "1";
    public static String CID;
    //用户类型 0：普通用户 1：管理员
    public static int USER_TYPE = 0;

    public static int VERSION_ID = 1;

    public static boolean latestVersion = true;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        EmojiManager.install(new IosEmojiProvider());
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.gray_bg, R.color.gray_99);
                return new ClassicsHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setAccentColorId(R.color.gray_99);
            }
        });
    }
}
