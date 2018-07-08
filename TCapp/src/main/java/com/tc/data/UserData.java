package com.tc.data;

import android.content.Context;

import com.google.gson.Gson;
import com.tc.activity.TcContentDetailActivity;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.utils.CommonUtil;
import com.tc.utils.StringUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by PB on 2017/12/5.
 */

public class UserData {

    /**
     * 找回密码
     * @param username
     * @param password
     * @param strCode
     * @return
     */
    public static RequestParams recoverPassword(String username,String password,String strCode)
    {
        RequestParams params = new RequestParams(Config.SERVER_API_URL+"user/recoverPassword");
        params.addQueryStringParameter("username",username);
        params.addQueryStringParameter("password", StringUtil.encodePassword(password, "md5"));
        params.addQueryStringParameter("code",strCode);
        return params;
    }

}
