package com.tc.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.tc.activity.ReceivedCommentActivity;
import com.tc.activity.ReceivedLikeActivity;
import com.tc.application.App;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.model.Notification;
import com.tc.receiver.NotificationBroadCastReceiver;

import java.lang.reflect.Type;

/**
 * Created by PB on 2017/11/27.
 */

public class IntentService extends GTIntentService {

    //    Context context = getApplicationContext();
    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String s) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + s);
        App.CID = s;
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {

        String message = new String(gtTransmitMessage.getPayload());
        Gson gson = new Gson();
        Log.i("TransmitMessage--->", message);
        Type jsonType = new TypeToken<Notification>() {
        }.getType();
        Notification notification = gson.fromJson(message, jsonType);

        //避免同一设备登录两个帐号而出现消息通知错误的问题
        if (!notification.getUserId().equals(App.USER_ID))
            return;
        Intent intent = new Intent(context, NotificationBroadCastReceiver.class);
        String content = "";
        int notifyType = 0;
        switch (notification.getNotificationType()) {
            case 0:
                //  intent = new Intent(this, ReceivedCommentActivity.class);
                content = "有人评论了您";
                notifyType = 3;
                Constant.new_comment = true;
                break;
            case 1:
                //   intent = new Intent(this, ReceivedLikeActivity.class);
                content = "有人赞了您";
                notifyType = 4;
                Constant.new_like = true;
                break;
            case 2:
                //intent = new Intent(this, ReceivedCommentActivity.class);
                content = "您收到一条系统消息";
                Constant.new_system_message = true;
                notifyType = 5;
                break;
            case 3:
                //其他设备登录，发送广播，通知强制下线
                Intent forceOfflineIntent = new Intent(Constant.FORCE_OFFLINE);
                sendBroadcast(forceOfflineIntent);
                return;
        }


        if (!notification.isNotify()) {
            Constant.new_system_message = true;
            return;
        }

        intent.putExtra("type", notification.getNotificationType());
        intent.setAction(Constant.NOTIFICATION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //0:评论消息已读，1：点赞消息已读，2:系统消息已读 3：新的评论消息，4：新的点赞消息,5：新的系统消息
        Intent notifyIntent = new Intent(Constant.MSG_NOTIFY);
        notifyIntent.putExtra(Constant.MSG_NOTIFY_TYPE, notifyType);
        context.sendBroadcast(notifyIntent);

        int id = 100;
        Intent deleteIntent = new Intent(context, NotificationBroadCastReceiver.class);
        deleteIntent.putExtra(Constant.NOTIFICATION_ID, id);
        deleteIntent.setAction(Constant.NOTIFICATION_CANCEL);
        deleteIntent.putExtra("type", notification.getNotificationType());
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 1, deleteIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Big Mouth")
                .setContentText(content)
                .setTicker("消息通知")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setDefaults(android.app.Notification.DEFAULT_VIBRATE)
                .setDeleteIntent(deletePendingIntent)
                .setContentIntent(pendingIntent);

        notificationManager.notify(id, builder.build());

    }


    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }
}
