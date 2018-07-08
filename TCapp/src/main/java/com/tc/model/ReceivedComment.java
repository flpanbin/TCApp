package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用于收到的评论
 * Created by PB on 2017/11/29.
 */

public class ReceivedComment {

    @SerializedName("comment_time")
    private String commentTime;
    @SerializedName("comment_nick_name")
    private String commentNickName;
    @SerializedName("comment_content")
    private String commentContent;
    @SerializedName("comment_avatar")
    private String commentAvatar;
    @SerializedName("comment_id")
    private String commentId;
    @SerializedName("comment_anonymous")
    private int commentAnonymous;

    public int getCommentAnonymous() {
        return commentAnonymous;
    }

    public void setCommentAnonymous(int commentAnonymous) {
        this.commentAnonymous = commentAnonymous;
    }

    public String getCommentId() {
        return commentId;
    }

    public ReceivedComment(String commentTime, String nickName, String commentContent, String avatar, String commentId, String fromUserId, TCContent tcContent) {
        this.commentTime = commentTime;
        this.commentNickName = nickName;
        this.commentContent = commentContent;
        this.commentAvatar = avatar;
        this.commentId = commentId;
        this.fromUserId = fromUserId;
        this.tcContent = tcContent;
    }

    public ReceivedComment(String commentTime, String nickName, String commentContent, String avatar, String fromUserId, TCContent tcContent) {
        this.commentTime = commentTime;
        this.commentNickName = nickName;
        this.commentContent = commentContent;
        this.commentAvatar = avatar;
        this.fromUserId = fromUserId;
        this.tcContent = tcContent;
    }


    public String getCommentAvatar() {

        return commentAvatar;
    }

    public void setCommentAvatar(String commentAvatar) {
        this.commentAvatar = commentAvatar;
    }

    public ReceivedComment() {
    }

    public ReceivedComment(String commentTime, String nickName, String commentContent, String fromUserId, TCContent tcContent) {

        this.commentTime = commentTime;
        this.commentNickName = nickName;
        this.commentContent = commentContent;
        this.fromUserId = fromUserId;
        this.tcContent = tcContent;
    }

    public String getCommentNickName() {

        return commentNickName;
    }

    public void setCommentNickName(String commentNickName) {
        this.commentNickName = commentNickName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
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

    @SerializedName("from_user_id")
    private String fromUserId;
    @SerializedName("content")
    private TCContent tcContent;


}
