package com.tc.adapter;

import android.content.Context;
import android.widget.BaseAdapter;


import java.util.List;

/**
 * Created by PB on 2017/8/23.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {

    private List<T> datas;
    private Context context;

    public CommonAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
