package com.tc.utils;

import android.content.Context;
import android.view.View;

import com.example.pb.myapplication.R;
import com.tc.data.CommonData;
import com.tc.view.CommonDialog;

/**
 * Created by PB on 2017/12/5.
 */

public class Report {
    public static void report(Context context, View v, String commentId, String contentId) {
        int type = 0;
        switch (v.getId()) {
            case R.id.tv_divulge_privacy:
                type = 1;
                break;
            case R.id.tv_personal_attack:
                type = 2;
                break;
            case R.id.tv_obscenity:
                type = 3;
                break;
            case R.id.tv_adv:
                type = 4;
                break;
            case R.id.tv_false_information:
                type = 5;
                break;
            case R.id.tv_llegal_information:
                type = 6;
                break;
            case R.id.tv_other:
                type = 7;
                break;
        }

        CommonData.report(context, type, commentId, contentId);

    }
}
