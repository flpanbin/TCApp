package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PB on 2017/7/23.
 */

public class TcType {

    @SerializedName("type_id")
    private String typeId;
    @SerializedName("type_name")
    private String typeName;
    @SerializedName("up_type_id")
    private String upTypeId;
    @SerializedName("type_icon")
    private String typeIcon;

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getTypeIcon() {
        return typeIcon;
    }

    public void setTypeIcon(String typeIcon) {
        this.typeIcon = typeIcon;
    }

    public String getUpTypeId() {
        return upTypeId;
    }

    public void setUpTypeId(String upTypeId) {
        this.upTypeId = upTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public TcType(String typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    @SerializedName("school_id")
    private String schoolId;

}
