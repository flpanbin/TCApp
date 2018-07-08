package com.tc.adapter;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by wc on 2017/9/3.
 */

public class ControlArrayAdapter<T> extends ArrayAdapter<T> {
    private static final int MAX_ITEM_COUNT = 4;
    private List<T> mItemList = null;
    public ControlArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        mItemList = objects;
    }

    @Override
    public int getCount() {
        if (mItemList == null)
            return 0;
        return Math.min(MAX_ITEM_COUNT,mItemList.size());
    }

    @Override
    public void clear() {
        super.clear();
        mItemList.removeAll(mItemList);
    }

}
