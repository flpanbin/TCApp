package com.tc.data;

import android.content.Context;


import com.tc.model.User;
import com.tc.utils.SPUtil;

/**
 * 用于保存用户个人信息
 * Created by PB on 2017/8/7.
 */

public class UserPreference {

    public final static String USER_NAME = "username";
    public final static String USER_PWD = "password";
    public final static String USER_TOKEN = "token";
    public final static String USER_ID = "userId";
    public final static String USER_NICK_NAME = "nickname";
    public final static String USER_ENTRANCE_TIME = "entrancreTime";
    public final static String USER_BIRTHDAY = "birthday";
    public final static String USER_SEX = "sex";
    public final static String USER_IDENTITY_TYPE = "identityType";
    public final static String USER_AVATAR = "avatar";
    public final static String USER_TYPE = "user_type";
    public final static String FIRST_LOGIN = "first_login";


    public static void updateUserInfo(Context context, User user) {
        SPUtil.put(context, UserPreference.USER_NAME, user.getUserName());
        SPUtil.put(context, UserPreference.USER_ID, user.getUserId());
        SPUtil.put(context, UserPreference.USER_NICK_NAME, user.getNickName());
        SPUtil.put(context, UserPreference.USER_ENTRANCE_TIME, user.getEntranceTime());
        SPUtil.put(context, UserPreference.USER_BIRTHDAY, user.getBirthday());
        SPUtil.put(context, UserPreference.USER_SEX, user.getSex());
        SPUtil.put(context, UserPreference.USER_IDENTITY_TYPE, user.getIdentityType());
        SPUtil.put(context, UserPreference.USER_AVATAR, user.getAvatar());
        SPUtil.put(context, UserPreference.USER_TYPE, user.getUserType());
    }

    public static void savePwd(Context context, String pwd) {
        SPUtil.put(context, UserPreference.USER_PWD, pwd);
    }

    public static void saveToken(Context context, String token) {
        SPUtil.put(context, UserPreference.USER_TOKEN, token);
    }

    public static void clearToken(Context context) {
        SPUtil.put(context, UserPreference.USER_TOKEN, "");
    }

    public static String getUserNickName(Context context) {
        return (String) SPUtil.get(context, USER_NICK_NAME, "");
    }

    public static String getUserAvatar(Context context) {
        return (String) SPUtil.get(context, USER_AVATAR, "");
    }

    public static void firstLogin(Context context, boolean first) {
        SPUtil.put(context, FIRST_LOGIN, false);
    }

    public static boolean isFirstLogin(Context context) {
        return (boolean) SPUtil.get(context, FIRST_LOGIN, true);
    }

    public static int getUserType(Context context) {
        return (int) SPUtil.get(context, USER_TYPE, 0);
    }
}
