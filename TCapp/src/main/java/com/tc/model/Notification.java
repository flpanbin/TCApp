package com.tc.model;

/**
 * Created by PB on 2017/12/9.
 */

public class Notification {
    //0:评论通知;1:点赞通知;2:系统消息通知
    private int notificationType;
    private String userId;
    private boolean notify;

    public boolean isNotify() {
        return notify;
    }

    public Notification(int notificationType, String userId, boolean notify) {
        this.notificationType = notificationType;
        this.userId = userId;
        this.notify = notify;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationType=" + notificationType +
                ", userId='" + userId + '\'' +
                '}';
    }

    public Notification() {
    }

    public Notification(int notificationType, String userId) {

        this.notificationType = notificationType;
        this.userId = userId;
    }

    public int getNotificationType() {

        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
