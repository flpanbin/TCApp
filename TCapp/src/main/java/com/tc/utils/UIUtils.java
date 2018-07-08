package com.tc.utils;

import android.widget.ImageView;

import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.data.UserPreference;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by PB on 2018/3/8.
 */

public class UIUtils {

    /**
     * 显示头像 如果没有设置头像则显示默认头像
     * @param imageView
     * @param avatarName
     */
    public static void showAvatar(ImageView imageView, String avatarName) {
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        String avatarPath;
        if(StringUtil.isEmpty(avatarName))
        {
            avatarPath = Config.AVATAR_PATH + Constant.DEFAULT_AVATAR;
        }else {
            avatarPath = Config.AVATAR_PATH +avatarName;
        }
        x.image().bind(imageView,avatarPath, imageOptions);
    }

}
