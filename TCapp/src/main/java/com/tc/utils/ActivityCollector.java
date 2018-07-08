package com.tc.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于管理activity的生命周期
 * Created by PB on 2017/6/10.
 */

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
