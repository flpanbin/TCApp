package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用于收到的评论
 * Created by PB on 2017/11/29.
 */

public class ReceivedLike {

    @SerializedName("like_time")
    private String likeTime;
    @SerializedName("nick_name")
    private String nickName;
    @SerializedName("from_user_id")
    private String fromUserId;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("content")
    private TCContent tcContent;

    public ReceivedLike(String likeTime, String nickName, String fromUserId, String avatar, TCContent tcContent) {
        this.likeTime = likeTime;
        this.nickName = nickName;
        this.fromUserId = fromUserId;
        this.avatar = avatar;
        this.tcContent = tcContent;
    }

    public String getLikeTime() {
        return likeTime;
    }

    public String getNickName() {
        return nickName;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getAvatar() {
        return avatar;
    }

    public TCContent getTcContent() {
        return tcContent;
    }
}
