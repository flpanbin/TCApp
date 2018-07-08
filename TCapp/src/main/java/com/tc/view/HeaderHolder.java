package com.tc.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pb.myapplication.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by PB on 2017/6/18.
 */

public class HeaderHolder {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.iv_back)
    private ImageView iv_back;
    @ViewInject(R.id.tv_right)
    private TextView tv_right;

    public HeaderHolder init(final Activity activity, CharSequence title) {
        View view = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        x.view().inject(this, view);
        tv_title.setText(title);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        setRightText(null, null);
        return this;
    }


    public HeaderHolder setLeftBtn(View.OnClickListener onClick) {
        if (onClick == null) {
            iv_back.setVisibility(View.INVISIBLE);
        } else {
            iv_back.setVisibility(View.VISIBLE);
            iv_back.setOnClickListener(onClick);
        }
        return this;
    }

    public HeaderHolder setRightText(CharSequence text, View.OnClickListener onClick) {
        if (onClick == null) {
            tv_right.setOnClickListener(null);
            tv_right.setVisibility(View.INVISIBLE);
        } else {
            tv_right.setText(text);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setOnClickListener(onClick);
        }
        return this;
    }


    /**
     * 隐藏右边text
     *
     * @return
     */
    public HeaderHolder hideRightText() {
        setRightText(null, null);
        return this;
    }
}
