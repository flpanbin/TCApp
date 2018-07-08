package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.conf.Config;
import com.tc.model.ReceivedLike;
import com.tc.utils.DateUtil;
import com.tc.utils.StringUtil;
import com.vanniktech.emoji.EmojiTextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.List;

/**
 * 收到的点赞
 * Created by PB on 2017/11/29.
 */

public class ReceivedUpAdapter extends CommonAdapter<ReceivedLike> {

    private Context context;
    private List<ReceivedLike> datas;

    public ReceivedUpAdapter(Context context, List<ReceivedLike> datas) {
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
        ReceivedLike receivedLike = datas.get(position);
        holder.tv_nickname.setText(receivedLike.getNickName());
        holder.tv_comment.setText("赞了你的吐槽");
        //
        try {
            holder.tv_time.setText(DateUtil.getChatTimeStr(DateUtil.getTime(receivedLike.getLikeTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_content.setText(receivedLike.getTcContent().getTcText());
        if (StringUtil.isEmpty(receivedLike.getTcContent().getTcTargetId())) {
            holder.tv_target.setVisibility(View.GONE);
        } else {
            holder.tv_target.setText(receivedLike.getTcContent().getTargetName());
            holder.tv_target.setVisibility(View.VISIBLE);
        }
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(holder.iv_avatar, Config.AVATAR_PATH + receivedLike.getAvatar(), imageOptions);
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
