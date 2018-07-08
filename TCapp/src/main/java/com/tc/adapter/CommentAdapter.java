package com.tc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.activity.TcContentDetailActivity;
import com.tc.application.App;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.model.Comment;

import com.tc.utils.DateUtil;
import com.tc.utils.EmotionUtils;
import com.tc.utils.SpanStringUtils;
import com.tc.utils.StringUtil;
import com.vanniktech.emoji.EmojiTextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.List;


/**
 * Created by PB on 2017/7/30.
 */

public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> datas;
    private ImageOptions imageOptions;
    private Comment comment;
    private SimpleAdapter adapter;

    public CommentAdapter(Context context, List<Comment> datas) {
        this.context = context;
        this.datas = datas;
        imageOptions = new ImageOptions.Builder().setCircular(true).build();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_content_comment, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        comment = datas.get(position);

       // ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        if (comment.getAnonymous() == 1) {
            holder.tv_nickname.setText("匿名");
            x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + Constant.DEFAULT_AVATAR, imageOptions);
        } else {
            holder.tv_nickname.setText(comment.getNickName());
            x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + comment.getUserAvatar(), imageOptions);
        }
        //
        try {
            holder.tv_time.setText(DateUtil.getChatTimeStr(DateUtil.getTime(comment.getCommentTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_comment.setText(comment.getCommentContent());
        String toNickName = comment.getToNickName();
//        if (comment.getAnonymous() == 1) {
//            toNickName = "匿名";
//        }
        if (!StringUtil.isEmpty(comment.getToNickName())) {
            holder.tv_to_comment.setVisibility(View.VISIBLE);
            holder.tv_to_comment.setText("回复@" + toNickName + "的评论: " + comment.getToCommentContent());
        } else {
            holder.tv_to_comment.setVisibility(View.GONE);
        }
        if (comment.getUserId().equals(App.USER_ID)) {
            holder.iv_comment.setVisibility(View.GONE);
        } else {
            holder.iv_comment.setVisibility(View.VISIBLE);
        }
        holder.iv_comment.setTag(position);
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知更新指定评论信息
                TcContentDetailActivity.sendBroadCast(context,datas.get((int) v.getTag()));
            }
        });
        return convertView;
    }

//    private void sendBroadCast(Comment comment) {
//        Intent intent = new Intent();
//        intent.setAction("commentClick");
//        intent.putExtra("comment", comment);
//        context.sendBroadcast(intent);
//    }

}


class ViewHolder {
    @ViewInject(R.id.iv_avatar)
    ImageView iv_avatar;
    @ViewInject(R.id.tv_nickname)
    TextView tv_nickname;
    @ViewInject(R.id.tv_time)
    TextView tv_time;
    @ViewInject(R.id.iv_comment)
    ImageView iv_comment;
    @ViewInject(R.id.tv_comment)
    EmojiTextView tv_comment;
    @ViewInject(R.id.tv_to_comment)
    EmojiTextView tv_to_comment;

}

