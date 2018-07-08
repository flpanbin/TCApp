package com.tc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by PB on 2017/7/30.
 */

public class Comment implements Serializable {

    @SerializedName("comment_id")
    private String commentId;
    @SerializedName("content_id")
    private String contentId;
    @SerializedName("comment_content")
    private String commentContent;//回复内容
    @SerializedName("comment_time")
    private String commentTime;
    @SerializedName("avatar")
    private String userAvatar;
    @SerializedName("nick_name")
    private String nickName;//回复用户昵称
    @SerializedName("to_nick_name")
    private String toNickName; //被回复用户昵称
    @SerializedName("to_comment_content")
    private String toCommentContent;//被回复内容

    public int getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(int anonymous) {
        this.anonymous = anonymous;
    }

    @SerializedName("anonymous")
    private int anonymous;

    public String getToCommentContent() {
        return toCommentContent;
    }

    public void setToCommentContent(String toCommentContent) {
        this.toCommentContent = toCommentContent;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public Comment(String fromUserId, String commentId, String contentId, String commentContent, String commentTime) {
        this.userId = fromUserId;
        this.commentId = commentId;
        this.contentId = contentId;
        this.commentContent = commentContent;
        this.commentTime = commentTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Comment() {
    }

    @SerializedName("from_user_id")

    private String userId;

    public Comment(String commentId, String contentId, String commentContent, String commentTime, String fromUserId, String toUserId) {
        this.commentId = commentId;
        this.contentId = contentId;
        this.commentContent = commentContent;
        this.commentTime = commentTime;
        this.userId = fromUserId;
    }

    @Override
    public boolean equals(Object obj) {
        return this.commentId.equals(((Comment) obj).getCommentId());
    }
}
