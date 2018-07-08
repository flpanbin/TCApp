package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pb.myapplication.R;
import com.tc.utils.ImageUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

/**
 * Created by PB on 2017/8/23.
 */

public class SelectedPicAdapter extends CommonAdapter<String> {
    private Context context;
    List<String> datas;

    public SelectedPicAdapter(Context context, List<String> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas.size() == 9) {
            return 9;
        } else {
            return datas.size() + 1;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview_select_pic, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (position == datas.size()) {
                holder.iv_picture.setImageResource(R.drawable.icon_add_pic);
                holder.iv_close.setVisibility(View.GONE);
                // holder.iv_picture.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_add_pic));
                if (position == 9) {
                    holder.iv_picture.setVisibility(View.GONE);
                }
            } else {
                holder.iv_picture.setImageBitmap(ImageUtil.revitionImageSize(datas.get(position)));
                holder.iv_close.setVisibility(View.VISIBLE);
            }
            holder.iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < datas.size()) {
                        datas.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.iv_picture)
        ImageView iv_picture;
        @ViewInject(R.id.iv_close)
        ImageView iv_close;
    }
}
