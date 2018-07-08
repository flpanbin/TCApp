package com.tc.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.pb.myapplication.R;

/**
 * Created by PB on 2017/6/18.
 */

public class CommonDialog extends Dialog {
    private Activity activity;

    public CommonDialog(Context context) {
        super(context);
    }

    public CommonDialog(Context context, int style) {
        super(context, style);

    }

    /**
     * @param context
     * @param view
     * @param compute 是否需要计算尺寸
     */
    public CommonDialog(Context context, View view, boolean compute) {
        this(context, view, 0.7, 0.5, compute);
    }

    /**
     * @param context
     * @param layout
     * @param compute 是否需要计算尺寸
     */
    public CommonDialog(Context context, @LayoutRes int layout, boolean compute) {
        this(context, layout, 0.7, 0.5, compute);
    }

    public CommonDialog(Context context, @LayoutRes int layout, double width, double height, boolean compute) {
        super(context, R.style.my_dialog_style);
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(layout);
        setAttr((Activity) context, width, height, true);
    }

    private void setAttr(Activity context, double width, double height, boolean compute) {
        activity = context;
        if (compute) {
            computeDialogSize(width, height);
        } else {
            setDialogSize(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public CommonDialog(Context context, View view, double width, double height, boolean compute) {
        super(context, R.style.my_dialog_style);
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(view);
        activity = (Activity) context;
        setAttr((Activity) context, width, height, compute);
    }

    private void computeDialogSize(double width, double height) {
//        Window dialogWindow = getWindow();
//        WindowManager manager = activity.getWindowManager();
//        Display display = manager.getDefaultDisplay();
//        WindowManager.LayoutParams params = dialogWindow.getAttributes();
//        Point point = new Point();
//        display.getSize(point);
//        params.width = (int) (point.x * width);
//        params.height = (int) (point.y * height);
//        dialogWindow.setAttributes(params);

        WindowManager manager = activity.getWindowManager();
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        setDialogSize((int) (point.x * width), (int) (point.y * height));
    }

    private void setDialogSize(int width, int height) {
        Window dialogWindow = getWindow();
        WindowManager manager = activity.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = width;
        params.height = height;
        dialogWindow.setAttributes(params);
    }


}
