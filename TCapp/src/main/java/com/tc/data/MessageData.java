package com.tc.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.activity.MessageActivity;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.model.Message;
import com.tc.utils.CommonUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by PB on 2017/12/11.
 */

public class MessageData {

    public static RequestParams getMessage(final boolean refresh,int pageNum) {
        pageNum = refresh ? 1 : pageNum;
        Log.i("pageNum", pageNum + "");
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "message/getMessage");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addParameter("pageNum", pageNum);
        return  params;
    }
}
