package com.tc.model;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;

/**
 * Created by PB on 2017/6/11.
 */

public class TCContent implements Serializable {
    @SerializedName("content_id")
    private String contentId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("nick_name")
    private String userNickName;
    @SerializedName("content")
    private String tcText;
    @SerializedName("avatar")
    private String userAvatar;
    @SerializedName("count_like")
    private int likeCount;
    @SerializedName("count_comment")
    private int commentCount;
    @SerializedName("release_time")
    private String tcTime;
    @SerializedName("target_id")
    private String tcTargetId;
    @SerializedName("like_id")
    private String likeId;
    @SerializedName("pic_list")
    private String picList;
    @SerializedName("target_name")
    private String targetName;

    public String getTcTypeId() {
        return tcTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    @SerializedName("anonymous")

    private int anonymous;
    @SerializedName("type_id")
    private String tcTypeId;
    @SerializedName("type_name")
    private String typeName;

    public String getCollectId() {
        return collectId;
    }

    @SerializedName("collect_id")
    private String collectId;

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getPicList() {
        return picList;
    }

    public void setPicList(String picList) {
        this.picList = picList;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getTcTargetId() {
        return tcTargetId;
    }

    public void setTcTargetId(String tcTargetId) {
        this.tcTargetId = tcTargetId;
    }

    public TCContent() {
    }

    public int getAnonymous() {
        return anonymous;
    }

    public TCContent(String tcId, String userName, String userNickName, String tcText, String userAvastar, int upCount, int commentCount, String tcTime, String tcPhoto, boolean isUped, String target) {

        this.contentId = tcId;
        this.userId = userName;
        this.userNickName = userNickName;
        this.tcText = tcText;
        this.userAvatar = userAvastar;
        this.likeCount = upCount;
        this.commentCount = commentCount;
        this.tcTime = tcTime;
        this.tcPhoto = tcPhoto;
        this.isUped = isUped;
        this.tcTargetId = target;
    }

    public String getUserAvatar() {
        return userAvatar;

    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getTcTime() {
        return tcTime;
    }

    public void setTcTime(String tcTime) {
        this.tcTime = tcTime;
    }

    public String getTcPhoto() {
        return tcPhoto;
    }

    public void setTcPhoto(String tcPhoto) {
        this.tcPhoto = tcPhoto;
    }

    public boolean isUped() {
        return isUped;
    }

    public void setUped(boolean uped) {
        isUped = uped;
    }

    private String tcPhoto;
    private boolean isUped;

    public String getContentId() {
        return contentId;
    }


    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getTcText() {
        return tcText;
    }

    public void setTcText(String tcText) {
        this.tcText = tcText;
    }


    @Override
    public String toString() {
        return "TCContent{" +
                "contentId='" + contentId + '\'' +
                ", userId='" + userId + '\'' +
                ", userNickName='" + userNickName + '\'' +
                ", tcText='" + tcText + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", tcTime='" + tcTime + '\'' +
                ", tcTargetId='" + tcTargetId + '\'' +
                ", likeId='" + likeId + '\'' +
                ", picList='" + picList + '\'' +
                ", tcPhoto='" + tcPhoto + '\'' +
                ", isUped=" + isUped +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        TCContent tcContent = (TCContent) obj;
        return tcContent.getContentId().equals(this.getContentId());
    }
}
