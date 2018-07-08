package com.tc.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.pb.myapplication.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.activity.SplashActivity;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.model.Version;
import com.tc.utils.CommonUtil;
import com.tc.utils.SPUtil;
import com.tc.utils.UpdateManager;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;

/**
 * Created by PB on 2017/12/4.
 */

public class CommonData {
    /**
     * 举报
     *
     * @param context
     * @param type
     * @param commentId
     * @param contentId
     */
    public static void report(final Context context, int type, String commentId, String contentId) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/contentReport");
        params.addQueryStringParameter("commentId", commentId);
        params.addQueryStringParameter("contentId", contentId);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addParameter("type", type);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(context, "提交成功");
                } else {
                    CommonUtil.toast(context, "提交失败");
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

    public static RequestParams getActivities() {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "common/getActivities");
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        return params;
    }

    static ProgressDialog progressDialog;

    /**
     *
     * 获取最新的版本信息
     *
     * @param context
     * @param showProgressDialog  是否需要显示进度框
     * @param checkType    true: from welcome activity; false :other UI
     */
    public static void getVersion(final Context context, final boolean showProgressDialog, final boolean checkType) {
        if (showProgressDialog) {
            progressDialog = Windows.loading(context);
        }
        RequestParams params = new RequestParams(Config.VERSION_SERVER_API_URL + "common/getVersion");
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<Version>>() {
                }.getType();
                ResponseResult<Version> responce = gson.fromJson(result, jsonType);
                if (responce.getCode() == Code.SUCCESS) {
                    Version version = responce.getData();
                    if (version.getVersionCode() > UpdateManager.getInstance().getVersionCode(context)) {
                        App.latestVersion = false;
                        UpdateManager.getInstance().checkUpdate(context, version, checkType);
                    } else
                        {
                        // if from WelcomeActivity,then enter MainActivity
                        if(checkType)
                        {
                            if (UserPreference.isFirstLogin(context)) {
                                context.startActivity(new Intent(context, SplashActivity.class));
                            } else {
                                context.startActivity(new Intent(context, MainActivity.class));
                            }
                            ((Activity) context).finish();
                        }else
                            {
                            CommonUtil.toast(context,"已是最新版");
                        }
                        App.latestVersion = true;
                    }
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
                if (showProgressDialog)
                    progressDialog.dismiss();
            }
        });
    }
//    private void getVersion(Context context ,Version version){
//        String versionName = version.getVersionName();
//        String updateContet = version.getUpdateContet();
//        int versionCode = version.getVersionCode();
//        if(!versionName.isEmpty()&&!updateContet.isEmpty()&&versionCode!=-1){
//            saveVersionData(context,versionName,updateContet,versionCode);
//        }
//    }
//
//    private void saveVersionData(Context context,String versionName, String versionUpdateContent, int versionCode) {
//        SPUtil.put(context,"versionName",versionName);
//        SPUtil.put(context,"versionUpdateContent",versionUpdateContent);
//        SPUtil.put(context,"versionCode",versionCode);
//    }

    /**
     * 根据版本号获取版本信息
     * @param context
     * @return
     */
    public static RequestParams getVersionInfByVersionCode(Context context) {
        RequestParams params = new RequestParams(Config.VERSION_SERVER_API_URL + "common/getVersionInfoByVersionCode");
        params.addParameter("version_code",UpdateManager.getInstance().getVersionCode(context));
        return params;
    }
}
