package com.tc.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tc.activity.ReceivedCommentActivity;
import com.tc.activity.ReceivedLikeActivity;
import com.tc.activity.SystemMsgActivity;
import com.tc.data.Constant;

/**
 * Created by PB on 2017/12/10.
 */

public class NotificationBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(Constant.NOTIFICATION_CLICK)) {
            int type = intent.getIntExtra("type", -1);
            int notifyType=0;
            Intent jumpIntent = null;
            //0:comment 1:like 2: system
            switch (type) {
                case 0:
                    jumpIntent = new Intent(context, ReceivedCommentActivity.class);
                    notifyType = 0;
                    break;
                case 1:
                    jumpIntent = new Intent(context, ReceivedLikeActivity.class);
                    notifyType = 1;
                    break;
                case 2:
                    jumpIntent = new Intent(context, SystemMsgActivity.class);
                    notifyType = 2;
                    break;
            }
            //0:评论消息已读，1：点赞消息已读，2:系统消息已读 3：新的评论消息，4：新的点赞消息,5：新的系统消息
            Intent notifyIntent = new Intent(Constant.MSG_NOTIFY);
            notifyIntent.putExtra(Constant.MSG_NOTIFY_TYPE, notifyType);
            context.sendBroadcast(notifyIntent);

            if (jumpIntent != null) {
                jumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(jumpIntent);
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(100);

        } else if (action.equals(Constant.NOTIFICATION_CANCEL)) {
            int type = intent.getIntExtra("type", -1);
            switch (type) {
                case 0:
                    Constant.new_comment = true;
                    break;
                case 1:
                    Constant.new_like = true;
                    break;
                case 2:
                    Constant.new_system_message = true;
                    break;
            }
        }
    }
}
