package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PB on 2017/12/10.
 */

public class Message {
    @SerializedName("message_id")
    private String messageId;
    @SerializedName("content")
    private String content;
    @SerializedName("create_time")
    private String createTime;
    @SerializedName("user_id")
    private String userId;

    public Message() {
    }

    public Message(String messageId, String content, String createTime, String userId) {

        this.messageId = messageId;
        this.content = content;
        this.createTime = createTime;
        this.userId = userId;
    }

    public String getMessageId() {

        return messageId;
    }


    public String getContent() {
        return content;
    }

    public String getCreateTime() {
        return createTime;
    }


    public String getUserId() {
        return userId;
    }


}
