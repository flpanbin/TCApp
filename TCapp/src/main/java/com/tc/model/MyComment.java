package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * 用于我的回复
 * Created by PB on 2017/11/5.
 *
 */

public class MyComment {
    @SerializedName("comment_time")
    private String commentTime;
    @SerializedName("to_comment_content")
    private String toCommentContent;
    @SerializedName("to_user_id")
    private String toUserId;
    @SerializedName("to_nick_name")
    private String toNickName;
    @SerializedName("comment_content")
    private String commentContent;
    @SerializedName("content")
    private TCContent tcContent;
    @SerializedName("comment_id")
    private String commentId;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getToCommentContent() {
        return toCommentContent;
    }

    public void setToCommentContent(String toCommentContent) {
        this.toCommentContent = toCommentContent;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public TCContent getTcContent() {
        return tcContent;
    }

    public void setTcContent(TCContent tcContent) {
        this.tcContent = tcContent;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
    @Override
    public boolean equals(Object obj) {
        return this.commentId.equals(((MyComment)obj).getCommentId());
    }
}
