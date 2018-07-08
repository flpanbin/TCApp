package com.tc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;


import com.SuperKotlin.pictureviewer.ImagePagerActivity;
import com.SuperKotlin.pictureviewer.PictureConfig;
import com.bumptech.glide.Glide;
import com.example.pb.myapplication.R;
import com.tc.conf.Config;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by PB on 2017/6/11.
 */

public class TCContentPicAdapter extends CommonAdapter<String> {
    LayoutInflater inflater;
    ArrayList<String> imgList;
    Context context;

    public TCContentPicAdapter(Context context, ArrayList<String> datas) {
        super(context, datas);
        this.context = context;
        imgList = datas;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview_show_pic, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        ImageOptions options = new ImageOptions.Builder()
//                .setSize(100, 100)
//                .setLoadingDrawableId(R.drawable.icon_pic_default)
//                .setFailureDrawableId(R. drawable.icon_pic_failed)
//                .setUseMemCache(true).build();
//        x.image().bind(holder.imageView, imgList.get(position), options);
        Glide.with(context).load(imgList.get(position))
                .thumbnail(0.1f).crossFade()
                .placeholder(R.color.gray_ee)
                .error(R.drawable.icon_pic_failed)
                .into(holder.imageView);

//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PictureConfig.Builder builder = new PictureConfig.Builder();
//                builder.setListData(imgList);
//                builder.setPosition(position);
//                builder.setDownloadPath("tc_download");
//                builder.needDownload(true);
//                builder.setPlacrHolder(R.drawable.icon_pic_default);
//                PictureConfig config = builder.build();
//                ImagePagerActivity.startActivity(context, config);
//            }
//        });
        return convertView;
    }


    class ViewHolder {
        @ViewInject(R.id.imageView)
        ImageView imageView;
    }
}
