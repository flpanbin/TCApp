package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.tc.activity.SearchTcTargetActivity;
import com.tc.utils.SPUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by wc on 2017/8/31.
 */

public class HistoryKeyAdapter extends CommonAdapter<String> {

    private LayoutInflater inflater;
    private Context context;
    private List<String> datas;

    public HistoryKeyAdapter(Context context, List<String> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.item_search_history, parent, false);
        x.view().inject(holder, convertView);
        holder.tv_key.setText(datas.get(position));
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.remove(position);
                notifyDataSetChanged();
                saveHistoryKeys();
            }
        });
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.tv_key)
        TextView tv_key;
        @ViewInject(R.id.iv_delete)
        ImageView iv_delete;
    }

    private void saveHistoryKeys() {
        String keys = "";
        for (String key : datas) {
            keys = keys + "," + key;
        }
        keys = keys.substring(1, keys.length());
        SPUtil.put(context, SearchTcTargetActivity.HISTORY_KEY, keys);
    }
}
