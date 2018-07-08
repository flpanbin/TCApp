package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.model.ReceivedComment;
import com.tc.utils.DateUtil;
import com.tc.utils.StringUtil;
import com.vanniktech.emoji.EmojiTextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.List;

/**
 * Created by PB on 2017/11/29.
 */

public class ReceivedCommentAdapter extends CommonAdapter<ReceivedComment> {

    private Context context;
    private List<ReceivedComment> datas;

    public ReceivedCommentAdapter(Context context, List<ReceivedComment> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_received_comment, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ReceivedComment receivedComment = datas.get(position);
//        holder.tv_nickname.setText(receivedComment.getCommentNickName());
        holder.tv_comment.setText(receivedComment.getCommentContent());
//        holder.tv_time.setText(receivedComment.getCommentTime());
        try {
            holder.tv_time.setText(DateUtil.getChatTimeStr(DateUtil.getTime(receivedComment.getCommentTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_content.setText(receivedComment.getTcContent().getTcText());
        if (StringUtil.isEmpty(receivedComment.getTcContent().getTcTargetId())) {
            holder.tv_target.setVisibility(View.GONE);
        } else {
            holder.tv_target.setText(receivedComment.getTcContent().getTargetName());
            holder.tv_target.setVisibility(View.VISIBLE);
        }

        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
       // x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + receivedComment.getCommentAvatar(), imageOptions);

        if (receivedComment.getCommentAnonymous() == 1) {
            holder.tv_nickname.setText("匿名");
            x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + Constant.DEFAULT_AVATAR, imageOptions);
        } else {
            holder.tv_nickname.setText(receivedComment.getCommentNickName());
            x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + receivedComment.getCommentAvatar(), imageOptions);
        }
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.iv_avatar)
        ImageView iv_avatar;
        @ViewInject(R.id.tv_nickname)
        TextView tv_nickname;
        @ViewInject(R.id.tv_time)
        TextView tv_time;
        @ViewInject(R.id.tv_content)
        EmojiTextView tv_content;
        @ViewInject(R.id.tv_comment)
        EmojiTextView tv_comment;
        @ViewInject(R.id.tv_target)
        TextView tv_target;

    }

}
