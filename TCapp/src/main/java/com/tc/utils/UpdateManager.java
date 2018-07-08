package com.tc.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pb.myapplication.MainActivity;
import com.example.pb.myapplication.R;
import com.tc.activity.SplashActivity;
import com.tc.data.UserPreference;
import com.tc.model.Version;
import com.tc.view.CommonDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by PB on 2017/12/12.
 */

public class UpdateManager implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String fileName = null;//文件名
    private boolean isDownload = false;//是否下载
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private ProgressDialog progressDialog;
    public static UpdateManager updateManager;

    //true:首页检测版本更新；false:其他页面检测版本更新
    private boolean checkType = true;

    private Version versionInfo;
    private Context context;

    public static UpdateManager getInstance() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        return updateManager;
    }

    private UpdateManager() {

    }
//        dialog = new BaseDialog.Builder(context).setTitle(title).setMessage(updateMessage).setCancelable(cancelable)
//                .setLeftClick(left, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                        if (type == 1 | isDownload) {
//                            installApk(context, new File(downLoadPath, fileName));
//                        } else {
//                            if (url != null && !TextUtils.isEmpty(url)) {
//                                if (type == 2) {
//                                    createProgress(context);
//                                } else {
//                                    createNotification(context);
//                                }
//                                downloadFile(context);
//                            } else {
//                                Toast.makeText(context, "下载地址错误", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                })
//                .setRightClick("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                        if (type == 2) {
//                            System.exit(0);
//                        }
//                    }
//                })
//                .create();
//        dialog.show();


    private void cancelClicked() {
        updateDialog.dismiss();
        if (versionInfo.getForce() == 0) {
            ((Activity) context).finish();
        } else {
            if (!checkType) {
                updateDialog.dismiss();
                return;
            }

            if (UserPreference.isFirstLogin(context)) {
                context.startActivity(new Intent(context, SplashActivity.class));
            } else {
                context.startActivity(new Intent(context, MainActivity.class));
            }
            ((Activity) context).finish();
        }
    }

    private void updateClicked() {
        updateDialog.dismiss();
        if (isDownload) {
            installApk(context, new File(PathUtil.getDowloadPath(), fileName));
        } else {
            if (versionInfo.getForce() == 0) {
                createProgress(context);
            } else {
                createNotification(context);
                if (!checkType) {
                    updateDialog.dismiss();
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            downloadFile(context);
        }
    }

    CommonDialog updateDialog;

    /**
     * 下载apk
     */
    public void downloadFile(final Context context) {
        RequestParams params = new RequestParams(versionInfo.getLoadUrl());
        params.setSaveFilePath(PathUtil.getDowloadPath() + "/" + fileName);
        x.http().request(HttpMethod.GET, params, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //实时更新通知栏进度条
                if (versionInfo.getForce() == 1) {
                    notifyNotification(current, total);
                } else {
                    progressDialog.setProgress((int) (current * 100 / total));
                }
                if (total == current) {
                    if (versionInfo.getForce() == 0) {
                        progressDialog.setMessage("下载完成");
                    } else {
                        mBuilder.setContentText("下载完成");
                        mNotifyManager.notify(10086, mBuilder.build());
                    }

                    installApk(context, new File(PathUtil.getDowloadPath(), fileName));
                }
            }
        });
    }

    /**
     * 强制更新时显示在屏幕的进度条
     */
    private void createProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在下载...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    /**
     * 创建通知栏进度条
     */
    private void createNotification(Context context) {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("版本更新");
        mBuilder.setContentText("正在下载...");
        mBuilder.setProgress(0, 0, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(10086, notification);
    }

    /**
     * 更新通知栏进度条
     */
    private void notifyNotification(long percent, long length) {
        mBuilder.setProgress((int) length, (int) percent, false);
        mNotifyManager.notify(10086, mBuilder.build());
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件
     */
    private void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.example.pb.myapplication.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * @return 当前应用的版本号
     */
    public int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * @return 当前应用的版本号
     */
    public String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

//    /**
//     * 判断当前网络是否wifi
//     */
//    public boolean isWifi(Context mContext) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 版本更新
     */
    public void checkUpdate(Context context, Version version, boolean checkType) {
        this.versionInfo = version;
        this.context = context;
        this.checkType = checkType;
        verifyStoragePermissions((Activity) context);
        {
            //检测是否已下载
            String downLoadPath = PathUtil.getDowloadPath();
            File dir = new File(downLoadPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String loadUrl = versionInfo.getLoadUrl();
            fileName = versionInfo.getLoadUrl().substring(loadUrl.lastIndexOf("/") + 1, loadUrl.length());
            if (StringUtil.isEmpty(fileName) && !fileName.contains(".apk")) {
                fileName = context.getPackageName() + ".apk";
            }
            File file = new File(downLoadPath + "/" + fileName);

            isDownload = file.exists() ? true : false;

            updateDialog = Windows.createUpdateDialog(context, versionInfo, isDownload, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_left:
                            cancelClicked();
                            break;
                        case R.id.tv_right:
                            updateClicked();
                            break;
                    }
                }
            });
            updateDialog.show();
            //设置参数
            //UpdateManager.getInstance().setType(type).setUrl(url).setUpdateMessage(updateMessage).setFileName(fileName).setIsDownload(file.exists());
//            if (type == 1 && !file.exists()) {
//                UpdateManager.getInstance().downloadFile(context);
//            } else {
//                UpdateManager.getInstance().showDialog(context);
//            }

        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("UpdateManagerPermission", "yes");
            } else {
                Log.i("UpdateManagerPermission", "no");
            }
        }
    }


}
