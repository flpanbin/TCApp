package com.tc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pb.myapplication.R;
import com.tc.activity.TcContentDetailActivity;
import com.tc.model.TCContent;
import com.tc.adapter.holder.TcContentItemHolder;
import com.tc.view.BlankClicksGridView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.ParseException;
import java.util.List;

/**
 * Created by PB on 2017/6/11.
 */

public class TCContentAdapter extends CommonAdapter<TCContent> {
    Context context;
    List<TCContent> datas;
    LayoutInflater inflater;
    ImageOptions imageOptions;

    private boolean targetClickable = true;

    public TCContentAdapter(Context context, List<TCContent> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
        imageOptions = new ImageOptions.Builder().setCircular(true).build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TcContentItemHolder holder = null;
        if (convertView == null) {
            holder = new TcContentItemHolder();
            convertView = inflater.inflate(R.layout.item_tc_content_listview, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (TcContentItemHolder) convertView.getTag();
        }


        holder.setOnCommentClickListener(new TcContentItemHolder.OnCommentClickListener() {
            @Override
            public void onCommentClick() {
                startDetailActivity(position);
            }
        });
        holder.gv_pic.setOnTouchInvalidPositionListener(new BlankClicksGridView.OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int event) {
                return false;//不消耗点击事件，由父级控件处理
            }
        });

        final TCContent tcContent = datas.get(position);
        try {
            holder.bindView(tcContent, context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_target.setClickable(targetClickable);
        holder.setOnDeleteClickListener(new TcContentItemHolder.OnDeleteClickListener() {
            @Override
            public void onDeleteClick() {
                datas.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private void startDetailActivity(int position) {
        Intent intent = new Intent(context, TcContentDetailActivity.class);
        intent.putExtra("content", datas.get(position));
        context.startActivity(intent);
    }

    public void setTargetClickable(boolean visiable) {
        targetClickable = visiable;
    }


}
