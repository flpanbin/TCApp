package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.model.TcType;
import com.tc.conf.Config;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by PB on 2017/7/23.
 */

public class TCTypeAdapter extends CommonAdapter<TcType> {

    Context context;
    List<TcType> datas;
    LayoutInflater inflater;

    public TCTypeAdapter(Context context, List<TcType> datas) {
        super(context,datas);
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_gridview_type, parent, false);
            convertView.setTag(holder);
            x.view().inject(holder, convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TcType tcType = datas.get(position);
        holder.tv_type.setText(tcType.getTypeName());
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.tv_type)
        TextView tv_type;
    }
}
