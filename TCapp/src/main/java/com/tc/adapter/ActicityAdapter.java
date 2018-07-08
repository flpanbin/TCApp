package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.activity.BaseActivity;
import com.tc.conf.Config;
import com.tc.model.Activity;
import com.tc.model.Message;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by PB on 2017/12/11.
 */

public class ActicityAdapter extends CommonAdapter<Activity> {
    Context context;
    List<Activity> datas;

    public ActicityAdapter(Context context, List<Activity> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Activity activity = datas.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_activity, parent, false);
            holder = new ViewHolder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.tv_name.setText(activity.getActivityName());
//        holder.tv_brief.setText(activity.getActivityBrief());
//        holder.tv_end_time.setText("截至时间到" + activity.getEndTime());
        ImageOptions imageOptions = new ImageOptions.Builder().setImageScaleType(ImageView.ScaleType.FIT_XY).build();
         x.image().bind(holder.iv_image, Config.ACTIVITY_PATH + activity.getImage(), imageOptions);
//        x.image().bind(holder.iv_image, Config.ACTIVITY_PATH + activity.getImage());
//        holder.iv_image.setImageResource(R.drawable.banner_tcw);
        return convertView;
    }

    private class ViewHolder {
        //        @ViewInject(R.id.tv_name)
//        TextView tv_name;
//        @ViewInject(R.id.tv_brief)
//        TextView tv_brief;
//        @ViewInject(R.id.tv_end_time)
//        TextView tv_end_time;
        @ViewInject(R.id.iv_image)
        ImageView iv_image;
    }

}
