package com.tc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PB on 2017/12/12.
 */

public class Version {

    @SerializedName("version_id")
    private String versionId;
    @SerializedName("version_code")
    private int versionCode;
    @SerializedName("update_content")
    private String updateContet;
    @SerializedName("load_url")
    private String loadUrl;
    @SerializedName("force")
    private int force;
    @SerializedName("version_name")
    private String versionName;

    public String getVersionId() {
        return versionId;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getUpdateContet() {
        return updateContet;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public int getForce() {
        return force;
    }

    public String getVersionName() {
        return versionName;
    }

    public Version(String versionId, int versionCode, String updateContet, String loadUrl, int force, String versionName) {

        this.versionId = versionId;
        this.versionCode = versionCode;
        this.updateContet = updateContet;
        this.loadUrl = loadUrl;
        this.force = force;
        this.versionName = versionName;
    }
}
