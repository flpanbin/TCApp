package com.tc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import static com.example.pb.myapplication.R.attr.position;

/**
 * Created by PB on 2017/9/7.
 * 添加空白处点击事件
 */

public class BlankClicksGridView extends GridView {


    public BlankClicksGridView(Context context) {
        super(context);
    }

    public BlankClicksGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlankClicksGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    OnTouchInvalidPositionListener onTouchInvalidPositionListener;

    public interface OnTouchInvalidPositionListener {
        boolean onTouchInvalidPosition(int event);
    }

    public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
        onTouchInvalidPositionListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onTouchInvalidPositionListener == null)
            return super.onTouchEvent(ev);
        if (!isEnabled()) {
            return isClickable() || isLongClickable();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE://移动
            case MotionEvent.ACTION_CANCEL://从一个控件滑动到其他控件
                return super.onTouchEvent(ev);
        }
        int position = pointToPosition((int) ev.getX(), (int) ev.getY());
        if (position == INVALID_POSITION) {
            //super.onTouchEvent(ev);
            return onTouchInvalidPositionListener.onTouchInvalidPosition(ev.getActionMasked());
        }
        return super.onTouchEvent(ev);
    }

}
