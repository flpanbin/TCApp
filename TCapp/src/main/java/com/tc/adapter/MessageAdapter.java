package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.model.Message;
import com.tc.utils.DateUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.List;

/**
 * Created by PB on 2017/12/11.
 */

public class MessageAdapter extends CommonAdapter<Message> {

    private Context context;
    private List<Message> datas;

    public MessageAdapter(Context context, List<Message> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Message message = datas.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_message, parent, false);
            holder = new ViewHolder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_content.setText(message.getContent());
        //
        try {
            holder.tv_time.setText(DateUtil.getChatTimeStr(DateUtil.getTime(message.getCreateTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.tv_time)
        TextView tv_time;
        @ViewInject(R.id.tv_content)
        TextView tv_content;
    }

}
