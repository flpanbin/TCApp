package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.model.TcTarget;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by wc on 2017/8/31.
 */

public class TcTargetAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private LayoutInflater inflater;
    private Context context;
    private List<TcTarget> data;

    public TcTargetAdapter(Context context, List<TcTarget> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.item_list_tc_target, parent, false);
        x.view().inject(holder, convertView);
        TcTarget tcTarget = data.get(position);

        //如果有小类就显示，如果没有则不显示
        if (tcTarget.getTypeId().equals(tcTarget.getUpTypeId())) {
            holder.tv_type.setVisibility(View.GONE);
        } else {
            holder.tv_type.setVisibility(View.VISIBLE);
            holder.tv_type.setText(tcTarget.getTypeName());
        }
        holder.tv_target.setText(tcTarget.getTargetName());
        return convertView;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class ViewHolder {
        @ViewInject(R.id.tv_target)
        TextView tv_target;
        @ViewInject(R.id.tv_type)
        TextView tv_type;
    }
}
