package com.tc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wc on 2017/8/31.
 */

public class TcTarget implements Serializable {
    @SerializedName("target_name")
    private String targetName;

    public TcTarget(String targetName, String typeId, String targetId, String upTypeId) {
        this.targetName = targetName;
        this.typeId = typeId;
        this.targetId = targetId;
        this.upTypeId = upTypeId;
    }

    @SerializedName("type_id")
    private String typeId;
    @SerializedName("target_id")
    private String targetId;
    @SerializedName("up_type_id")
    private String upTypeId;
    @SerializedName("up_type_name")
    private String upTypeName;

    public String getUpTypeName() {
        return upTypeName;
    }

    public void setUpTypeName(String upTypeName) {
        this.upTypeName = upTypeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @SerializedName("type_name")
    private String typeName;

    public String getUpTypeId() {
        return upTypeId;
    }

    public void setUpTypeId(String upTypeId) {
        this.upTypeId = upTypeId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}