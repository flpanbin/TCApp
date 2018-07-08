package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PB on 2017/12/11.
 */

public class Activity {
    @SerializedName("activity_id")
    private String activityId;
    @SerializedName("activity_name")
    private String activityName;
    @SerializedName("activity_url")
    private String activityUrl;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("activity_brief")
    private String activityBrief;
    @SerializedName("image")
    private String image;
    @SerializedName("activity_title")
    private String activityTitle;
    public Activity() {
    }

    public String getImage() {
        return image;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public Activity(String activityId, String activityName, String activityUrl, String endTime, String activityBrief, String image, String activityTitle) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.activityUrl = activityUrl;
        this.endTime = endTime;
        this.activityBrief = activityBrief;
        this.image = image;
        this.activityTitle = activityTitle;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getActivityUrl() {
        return activityUrl;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getActivityBrief() {
        return activityBrief;
    }
}
