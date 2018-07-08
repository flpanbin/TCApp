package com.tc.data;

import android.content.Context;

import com.google.gson.Gson;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.utils.CommonUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by PB on 2017/12/5.
 */

public class TcData {

    /**
     * 根据id获取吐槽详情信息
     *
     * @param contentId
     * @return
     */
    public static RequestParams getTcDetailById(String contentId) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "tc/getTcDetailById");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("contentId", contentId);
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        return params;

    }

    /**
     * 删除评论
     *
     * @param context
     * @param commentId
     * @param contentId
     */
    public static void deleteComment(final Context context, String commentId, String contentId) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/deleteComment");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("commentId", commentId);
        params.addQueryStringParameter("contentId", contentId);
        params.addQueryStringParameter("fromUserId", App.USER_ID);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() != Code.SUCCESS) {
                    CommonUtil.toast(context, response.getMessage());
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取用户收到的赞
     *
     * @return
     */
    public static RequestParams getLikeInfo(int pageNum) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getLikeInfo");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addParameter("pageNum", pageNum);
        return params;
    }

    public static RequestParams sendComment(String contentId, String strContent, String toUserId, String toCommentContent, String toCommentId, String toNickName, int anonymous) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/releaseTcContentComment");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("contentId", contentId);
        params.addQueryStringParameter("content", strContent);
        params.addQueryStringParameter("toUserId", toUserId);
        params.addQueryStringParameter("toCommentContent", toCommentContent);
        params.addQueryStringParameter("toCommentId", toCommentId);
        params.addQueryStringParameter("toNickName", toNickName);
        params.addQueryStringParameter("anonymous", anonymous + "");
        return  params;
    }
}
