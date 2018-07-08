package com.tc.model;


import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    private String userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("nick_name")
    private String nickName;
    private String password;
    private int sex;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSchool() {return school;}

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getIdentityType() {
        return identityType;
    }

    public void setIdentityType(int identityType) {
        this.identityType = identityType;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", school='" + school + '\'' +
                ", createTime=" + createTime +
                ", avatar='" + avatar + '\'' +
                ", birthday='" + birthday + '\'' +
                ", identityType='" + identityType + '\'' +
                ", entranceTime='" + entranceTime + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getEntranceTime() {
        return entranceTime;
    }

    public void setEntranceTime(String entranceTime) {
        this.entranceTime = entranceTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String school;
    @SerializedName("create_time")
    private String createTime;
    private String avatar;
    private String birthday;
    @SerializedName("identity_type")
    private int identityType;
    @SerializedName("entrance_time")
    private String entranceTime;
    private String token;

    //用户类型     0：普通用户  1：管理员
    @SerializedName("user_type")
    private int userType;

    public int getUserType() {
        return userType;
    }
}
