package com.tc.adapter.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SuperKotlin.pictureviewer.ImagePagerActivity;
import com.SuperKotlin.pictureviewer.PictureConfig;
import com.bumptech.glide.Glide;
import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.tc.activity.TcContentAboutTargetActivity;
import com.tc.adapter.TCContentPicAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.fragment.SquareFragment;
import com.tc.model.TCContent;
import com.tc.utils.CommonUtil;
import com.tc.utils.DateUtil;
import com.tc.utils.EmotionUtils;
import com.tc.utils.SpanStringUtils;
import com.tc.utils.StringUtil;
import com.tc.utils.UIUtils;
import com.tc.view.CommonDialog;
import com.tc.view.BlankClicksGridView;
import com.vanniktech.emoji.EmojiTextView;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * Created by PB on 2017/7/30.
 */

public class TcContentItemHolder implements View.OnClickListener {

    @ViewInject(R.id.iv_avatar)
    public ImageView iv_avatar;
    @ViewInject(R.id.tv_nickname)
    public TextView tv_nickname;
    @ViewInject(R.id.tv_time)
    public TextView tv_time;
    @ViewInject(R.id.tv_content)
    public EmojiTextView tv_content;
    @ViewInject(R.id.tv_up_count)
    public TextView tv_up_count;
    @ViewInject(R.id.tv_comment_count)
    public TextView tv_comment_count;
    @ViewInject(R.id.tv_target)
    public TextView tv_target;
    @ViewInject(R.id.iv_up)
    public ImageView iv_up;
    @ViewInject(R.id.iv_comment)
    public ImageView iv_comment;
    @ViewInject(R.id.gv_pic)
    public BlankClicksGridView gv_pic;
    @ViewInject(R.id.iv_pic)
    ImageView iv_pic;
//    @ViewInject(R.id.iv_collect)
//    public ImageView iv_collect;

    @ViewInject(R.id.ll_like)
    public LinearLayout ll_up;
    @ViewInject(R.id.ll_comment)
    public LinearLayout ll_comment;
    @ViewInject(R.id.ll_more)
    public LinearLayout ll_more;

    public TextView tv_collect;
    public TextView tv_report;
    public TextView tv_delete;


    private OnCommentClickListener commentClickListener;

    Context context;
    TCContent content;

    public void bindView(TCContent content, Context context) throws ParseException {

        if (content.getAnonymous() == 1) {
            tv_nickname.setText("匿名");
            UIUtils.showAvatar(iv_avatar, "");
        } else {
            tv_nickname.setText(content.getUserNickName());
            UIUtils.showAvatar(iv_avatar, content.getUserAvatar());
        }

        tv_time.setText(DateUtil.getChatTimeStr(DateUtil.getTime(content.getTcTime())));
        tv_content.setText(content.getTcText());
        tv_up_count.setText(content.getLikeCount() + "");
        tv_comment_count.setText(content.getCommentCount() + "");

        if (StringUtil.isEmpty(content.getTcTargetId())) {
            tv_target.setVisibility(View.GONE);
        } else {
            tv_target.setVisibility(View.VISIBLE);
            tv_target.setText(content.getTargetName());
        }

        if (StringUtil.isEmpty(content.getLikeId())) {
            setImage(R.drawable.icon_up, iv_up);
        } else {
            setImage(R.drawable.icon_up_selected, iv_up);
        }

        this.context = context;
        this.content = content;
        ll_up.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_more.setOnClickListener(this);
        tv_target.setOnClickListener(this);


        if (!StringUtil.isEmpty(content.getPicList())) {
            showPhoto(content.getPicList());
        } else {
            gv_pic.setVisibility(View.GONE);
            iv_pic.setVisibility(View.GONE);
        }

        iv_pic.setOnClickListener(this);

        createSelectFunDialog();
        createSelectReportTypeDialog();
        if (StringUtil.isEmpty(content.getCollectId())) {
            tv_collect.setText("收藏");

        } else {
            tv_collect.setText("取消收藏");
        }
    }

    ArrayList<String> tempList;

    private void showPhoto(String picList) {
        String[] names = picList.split(",");
        final List<String> imgList = Arrays.asList(names);

        tempList = new ArrayList<>();
        for (String name : imgList) {
            tempList.add(Config.PIC_PATH + name);
        }

        if (tempList.size() == 1) {
            gv_pic.setVisibility(View.GONE);
            iv_pic.setVisibility(View.VISIBLE);

//            if (iv_pic.getTag(R.id.iv_pic) != null && !iv_pic.getTag(R.id.iv_pic).equals(tempList.get(0))) {
//                iv_pic.setImageResource(R.drawable.ic_default_image);
//            }
//            iv_pic.setTag(R.id.iv_pic, tempList.get(0));

            Glide.with(context)
                    .load(tempList.get(0)).crossFade()
                    .into(iv_pic);
            // if (iv_pic.getTag(R.id.iv_pic) != null && iv_pic.getTag(R.id.iv_pic).equals(tempList.get(0))) {
            //}
            return;
        } else if (tempList.size() > 1) {
            gv_pic.setVisibility(View.VISIBLE);
            iv_pic.setVisibility(View.GONE);
        } else {
            gv_pic.setVisibility(View.GONE);
            iv_pic.setVisibility(View.GONE);
            return;
        }
        TCContentPicAdapter adapter = new TCContentPicAdapter(context, tempList);
        gv_pic.setAdapter(adapter);
        gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lookPic(position);
            }
        });
        setGridViewHeight(imgList);

    }

    /**
     * 设置gridview的高度
     */
    private void setGridViewHeight(List<String> imgList) {
        ViewGroup.LayoutParams layoutParams = gv_pic.getLayoutParams();
        if (imgList.size() % 3 == 0) {
            layoutParams.height = (imgList.size() / 3) * DensityUtil.dip2px(100);
        } else {
            layoutParams.height = DensityUtil.dip2px(100) + (imgList.size() / 3) * DensityUtil.dip2px(100);
        }
        gv_pic.setLayoutParams(layoutParams);
    }

    private void setImage(int drawableId, ImageView imageView) {
        imageView.setImageResource(drawableId);
        imageView.setTag(drawableId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment:
                commentClickListener.onCommentClick();
                break;
            case R.id.ll_like:
                upOnClick(content.getContentId());
                break;
            case R.id.tv_target:
//                Intent intent = new Intent(context, TcContentAboutTargetActivity.class);
//                intent.putExtra("targetId", content.getTcTargetId());
//                intent.putExtra("targetName", content.getTargetName());
//                intent.putExtra("typeId", content.getTcTypeId());
//                intent.putExtra("typeName", content.getTypeName());
//                context.startActivity(intent);
                TcContentAboutTargetActivity.startActivity(context, content);
                break;
            case R.id.ll_more:
                funSelectDialog.show();
                break;
            case R.id.tv_collect:
                collectOnClick(content.getContentId());
                break;
            case R.id.tv_report:
                reportTypeSelectDialog.show();
                funSelectDialog.dismiss();
                break;
            case R.id.tv_delete:
                deleteContent(content.getContentId());
                SquareFragment.sendBroadCast(DELETE_CONTENT, context, content.getContentId());
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick();
                }
                funSelectDialog.dismiss();
                break;
            case R.id.tv_divulge_privacy:
                report(1);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.tv_personal_attack:
                report(2);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.tv_obscenity:
                report(3);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.tv_adv:
                report(4);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.tv_false_information:
                report(5);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.tv_llegal_information:
                report(6);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.tv_other:
                report(7);
                reportTypeSelectDialog.dismiss();
                break;
            case R.id.iv_pic:
                lookPic(0);
                break;
        }
    }

    private void lookPic(int position) {
        PictureConfig.Builder builder = new PictureConfig.Builder();
        builder.setListData(tempList);
        builder.setPosition(position);
        builder.setDownloadPath("tc_download");
        builder.needDownload(true);
        builder.setPlacrHolder(R.drawable.icon_pic_default);
        PictureConfig config = builder.build();
        ImagePagerActivity.startActivity(context, config);
    }

    private CommonDialog funSelectDialog;


    private void createSelectFunDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_function_content, null);
        tv_collect = (TextView) view.findViewById(R.id.tv_collect);
        tv_report = (TextView) view.findViewById(R.id.tv_report);
        tv_delete = (TextView) view.findViewById(R.id.tv_delete);

        if ((App.USER_TYPE == 1) || App.USER_ID.equals(content.getUserId())) {
            tv_delete.setVisibility(View.VISIBLE);
            tv_delete.setOnClickListener(this);
            view.findViewById(R.id.ll_divider).setVisibility(View.VISIBLE);
        } else {
            tv_delete.setVisibility(View.GONE);
            view.findViewById(R.id.ll_divider).setVisibility(View.GONE);
        }
        tv_collect.setOnClickListener(this);
        tv_report.setOnClickListener(this);
        funSelectDialog = new CommonDialog(context, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    private CommonDialog reportTypeSelectDialog;
    TextView tv_divulge_privacy;
    TextView tv_personal_attack;
    TextView tv_obscenity;
    TextView tv_adv;
    TextView tv_false_information;
    TextView tv_llegal_information;
    TextView tv_other;


    private void createSelectReportTypeDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_report_type, null);
        tv_divulge_privacy = (TextView) view.findViewById(R.id.tv_divulge_privacy);
        tv_personal_attack = (TextView) view.findViewById(R.id.tv_personal_attack);
        tv_obscenity = (TextView) view.findViewById(R.id.tv_obscenity);
        tv_adv = (TextView) view.findViewById(R.id.tv_adv);
        tv_false_information = (TextView) view.findViewById(R.id.tv_false_information);
        tv_llegal_information = (TextView) view.findViewById(R.id.tv_llegal_information);
        tv_other = (TextView) view.findViewById(R.id.tv_other);

        tv_divulge_privacy.setOnClickListener(this);
        tv_personal_attack.setOnClickListener(this);
        tv_obscenity.setOnClickListener(this);
        tv_adv.setOnClickListener(this);
        tv_false_information.setOnClickListener(this);
        tv_llegal_information.setOnClickListener(this);
        tv_other.setOnClickListener(this);
        reportTypeSelectDialog = new CommonDialog(context, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    public interface OnCommentClickListener {
        void onCommentClick();
    }

    public void setOnCommentClickListener(OnCommentClickListener clickListener) {
        this.commentClickListener = clickListener;
    }

    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setCommentCount(int count) {
        tv_comment_count.setText(count + "");
    }

    public void setUpCount(int count) {
        count = count > 0 ? count : 0;
        tv_up_count.setText(count + "");
    }


    /**
     * 点赞点击事件
     *
     * @param contentId
     */
    private void upOnClick(String contentId) {
        int count = 0;
        int drawableId = (int) iv_up.getTag();
        switch (drawableId) {
            case R.drawable.icon_up:
                setImage(R.drawable.icon_up_selected, iv_up);
                content.setLikeCount(content.getLikeCount() + 1);
                SquareFragment.sendBroadCast(UP, context, contentId);
                content.setLikeId("id");//解决ImageView状态复用问题
                break;
            case R.drawable.icon_up_selected:
                setImage(R.drawable.icon_up, iv_up);
                content.setLikeCount(content.getLikeCount() - 1);
                SquareFragment.sendBroadCast(CANCEL_UP, context, contentId);
                content.setLikeId("");
                break;
        }
        setUpCount(content.getLikeCount());
        upContent(contentId);
    }

    /**
     * 收藏点击事件
     *
     * @param contentId
     */
    private void collectOnClick(String contentId) {
        int count = 0;
        if (tv_collect.getText().toString().equals("收藏")) {
            SquareFragment.sendBroadCast(COLLECT, context, contentId);
            content.setCollectId("id");//解决ImageView状态复用的问题
            tv_collect.setText("取消收藏");
            funSelectDialog.dismiss();
        } else {
            SquareFragment.sendBroadCast(CANCEL_COLLECT, context, contentId);
            content.setCollectId("");
            tv_collect.setText("收藏");
            funSelectDialog.dismiss();
        }

        collectContent(contentId);

    }

    /**
     * 点赞
     *
     * @param contentId
     */
    private void upContent(String contentId) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/upContent");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("contentId", contentId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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
     * 收藏
     *
     * @param contentId
     */
    private void collectContent(String contentId) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/collectContent");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("contentId", contentId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    //点赞
    public static final int UP = 1;
    //取消点赞
    public static final int CANCEL_UP = 2;
    //评论
    public static final int COMMENT = 3;
    //删除评论
    public static final int DELETE_COMMENT = 7;
    //收藏
    public static final int COLLECT = 4;
    //取消收藏
    public static final int CANCEL_COLLECT = 5;
    //删除
    public static final int DELETE_CONTENT = 6;
    //评论
    public static final String ACTION = "refresh";

    //send broadcast to refresh ui
//    public void sendBroadCast(int action) {
//        Intent intent = new Intent();
//        intent.putExtra("contentId", content.getContentId());
//        intent.putExtra("action", action);
//        String className = ((Activity) context).getComponentName().getClassName();
//        Log.i("className", className);
//        intent.putExtra("className", className);
//        intent.setAction(ACTION);
//        context.sendBroadcast(intent);
//    }


    private void report(int type) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/contentReport");
        params.addQueryStringParameter("contentId", content.getContentId());
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addParameter("type", type);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ResponseResult response = gson.fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(context, "提交成功");
                } else {
                    CommonUtil.toast(context, "提交失败");
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

    public void deleteContent(String contentId) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/deleteTcContent");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("contentId", contentId);
        params.addQueryStringParameter("fromUserId",App.USER_ID);
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

}
