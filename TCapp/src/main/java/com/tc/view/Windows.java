package com.tc.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.model.Version;

/**
 * Created by PB on 2017/7/28.
 */

public class Windows {

    public static ProgressDialog loading(final Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.layout.dialog_loading_layout) {

            private ImageView iv_loading;

            @Override
            public void init() {
                iv_loading = (ImageView) findViewById(R.id.iv_loading);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_rotate);
                animation.setInterpolator(new LinearInterpolator());
                iv_loading.startAnimation(animation);
            }
        };
        dialog.show();
        return dialog;
    }

    public static CommonDialog createSelectReportTypeDialog(Context context, View.OnClickListener onClickListener) {
        TextView tv_divulge_privacy;
        TextView tv_personal_attack;
        TextView tv_obscenity;
        TextView tv_adv;
        TextView tv_false_information;
        TextView tv_llegal_information;
        TextView tv_other;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_report_type, null);
        tv_divulge_privacy = (TextView) view.findViewById(R.id.tv_divulge_privacy);
        tv_personal_attack = (TextView) view.findViewById(R.id.tv_personal_attack);
        tv_obscenity = (TextView) view.findViewById(R.id.tv_obscenity);
        tv_adv = (TextView) view.findViewById(R.id.tv_adv);
        tv_false_information = (TextView) view.findViewById(R.id.tv_false_information);
        tv_llegal_information = (TextView) view.findViewById(R.id.tv_llegal_information);
        tv_other = (TextView) view.findViewById(R.id.tv_other);

        tv_divulge_privacy.setOnClickListener(onClickListener);
        tv_personal_attack.setOnClickListener(onClickListener);
        tv_obscenity.setOnClickListener(onClickListener);
        tv_adv.setOnClickListener(onClickListener);
        tv_false_information.setOnClickListener(onClickListener);
        tv_llegal_information.setOnClickListener(onClickListener);
        tv_other.setOnClickListener(onClickListener);
        return new CommonDialog(context, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    public static CommonDialog createUpdateDialog(Context context, Version version, boolean download, View.OnClickListener onClickListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_left);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_right);

        //如果已经下载安装包
        if (download) {
            tv_ok.setText("立即安装");
            tv_title.setText("发现新版本");
        } else {
            tv_title.setText("强势升级");
            tv_ok.setText("马上下载");
        }
        tv_content.setText(version.getUpdateContet());
        tv_cancel.setOnClickListener(onClickListener);
        tv_ok.setOnClickListener(onClickListener);

        CommonDialog commonDialog = new CommonDialog(context, view, 0.8, 0.5, true);
        commonDialog.setCanceledOnTouchOutside(false);
        return commonDialog;
    }

    /**
     * create prompt dialog
     *
     * @param context
     * @param title
     * @param content
     * @param onClickListener clicklistener about cancel and ok button
     * @return
     */
    public static CommonDialog createPromtDialog(Context context, String title, String content, String leftText, String rightText, View.OnClickListener onClickListener,boolean canceledOnTouchOutside) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);

        TextView tv_left = (TextView) view.findViewById(R.id.tv_left);
        TextView tv_right = (TextView) view.findViewById(R.id.tv_right);

        if(TextUtils.isEmpty(leftText)){
            view.findViewById(R.id.ll_operate).setVisibility(View.GONE);
            view.findViewById(R.id.divider).setVisibility(View.GONE);
        }
        tv_title.setText(title);
        tv_left.setText(leftText);
        tv_right.setText(rightText);
        tv_content.setText(content);
        tv_left.setOnClickListener(onClickListener);
        tv_right.setOnClickListener(onClickListener);

        CommonDialog commonDialog = new CommonDialog(context, view, 0.8, 0.3, true);
        commonDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        return commonDialog;
    }


    public static CommonDialog createPromtDialog(Context context, String title, String content, View.OnClickListener onClickListener) {
        return createPromtDialog(context, title, content, "取消", "确定", onClickListener,false);
    }
    public static CommonDialog createPromtDialog(Context context, String title,String content,String okText,String cancelText,  View.OnClickListener onClickListener) {
        return createPromtDialog(context, title, content, cancelText,okText, onClickListener,false);
    }
    public static CommonDialog createPromtDialog(Context context, String title, String content) {
        return createPromtDialog(context, title, content, null, null,null,true);
    }

}
