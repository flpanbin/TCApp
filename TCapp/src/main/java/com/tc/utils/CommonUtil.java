package com.tc.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.tc.view.Windows;

import java.util.regex.Pattern;

/**
 * Created by PB on 2017/7/19.
 */

public class CommonUtil {
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static class Patterns {
        public static Pattern NUMBER = Pattern.compile("[0-9]+");
        public static Pattern MOBILE = Pattern.compile("^1(3|4|5|7|8)[0-9]{9}$");
    }

    /**
     * 如果键盘显示则隐藏，如果键盘隐藏则显示
     *
     * @param context
     */
    public static void manageInputMethod(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, 0);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     */
    public static void hideKeyBoard(Context context, IBinder token) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 初始化状态栏相关，
     * PS: 设置全屏需要在调用super.onCreate(arg0);之前设置setIsFullScreen(true);否则在Android 6.0下非全屏的activity会出错;
     * SDK19：可以设置状态栏透明，但是半透明的SYSTEM_BAR_BACKGROUNDS会不好看；
     * SDK21：可以设置状态栏颜色，并且可以清除SYSTEM_BAR_BACKGROUNDS，但是不能设置状态栏字体颜色（默认的白色字体在浅色背景下看不清楚）；
     * SDK23：可以设置状态栏为浅色（SYSTEM_UI_FLAG_LIGHT_STATUS_BAR），字体就回反转为黑色。
     * 为兼容目前效果，仅在SDK23才显示沉浸式。
     */
    public static void initStatusBar(Activity context) {
        Window win = context.getWindow();
        //KITKAT也能满足，只是SYSTEM_UI_FLAG_LIGHT_STATUS_BAR（状态栏字体颜色反转）只有在6.0才有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // 部分机型的statusbar会有半透明的黑色背景
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.setStatusBarColor(Color.TRANSPARENT);// SDK21
        }
    }

    public static void setBackgroundAlpha(float alpha, Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = alpha;
        activity.getWindow().setAttributes(params);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            //+- 数字为了扩大edittext区域 减少灵敏度
            int left = 0,
                    top = l[1] - 100,
                    bottom = top + v.getHeight() + 100,
                    right = left + v.getWidth() + 500;
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public static void hideVirtualKey(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
    }
}
