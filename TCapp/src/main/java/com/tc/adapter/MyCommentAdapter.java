package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.conf.Config;
import com.tc.data.UserPreference;
import com.tc.model.MyComment;
import com.tc.model.TCContent;
import com.tc.utils.DateUtil;
import com.tc.utils.EmotionUtils;
import com.tc.utils.SpanStringUtils;
import com.vanniktech.emoji.EmojiTextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.List;

/**
 * Created by PB on 2017/11/5.
 */

public class MyCommentAdapter extends CommonAdapter<MyComment> {
    private Context context;
    private List<MyComment> datas;

    public MyCommentAdapter(Context context, List<MyComment> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_my_comment, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyComment myComment = datas.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + UserPreference.getUserAvatar(context), imageOptions);

        //
        try {
            holder.tv_time.setText(DateUtil.getChatTimeStr(DateUtil.getTime(myComment.getCommentTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_nickname.setText(UserPreference.getUserNickName(context));
        //holder.tv_comment_content.setText(SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE, context, holder.tv_comment_content, myComment.getCommentContent()));

        holder.tv_comment_content.setText(myComment.getCommentContent());
        if (myComment.getToUserId() != null) {
            holder.tv_content_title.setText("@" + myComment.getToNickName() + "的评论");
            holder.tv_content.setText(myComment.getToCommentContent());
        } else {
            TCContent content = myComment.getTcContent();
            String nickName = "匿名";
            if (content.getAnonymous() == 0) {
                nickName = content.getUserNickName();
            }
            holder.tv_content_title.setText("@" + nickName + "的吐槽");
            // holder.tv_content.setText(SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE, context, holder.tv_content, content.getTcText()));
            holder.tv_content.setText(content.getTcText());
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
        @ViewInject(R.id.tv_comment_content)
        EmojiTextView tv_comment_content;
        @ViewInject(R.id.tv_content_title)
        TextView tv_content_title;
        @ViewInject(R.id.tv_content)
        EmojiTextView tv_content;
    }
}
