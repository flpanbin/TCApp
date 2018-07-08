package com.tc.utils;

import android.os.Environment;

/**
 * Created by PB on 2017/12/14.
 */

public class PathUtil {
    public static String getDowloadPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads";
    }
}
