package com.tc.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by PB on 2018/1/24.
 */

public class ScrollingTrickListView extends ListView {

    private View topView;

    public ScrollingTrickListView(Context context) {
        super(context);
    }

    public ScrollingTrickListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingTrickListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //添加TopView
    public void setTopView(View topView) {
        this.topView = topView;
    }

    float startY;
    float endY;
    float moveLen = 5;
    int direction;
    //head 是否显示
    boolean topViewShow;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (topView == null)
            return false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endY = ev.getY();
                if (endY - startY > moveLen) {
                    direction = 0;//向下滑动
                    if (!topViewShow) break;    //如果header没显示则不做处理
                    else {
                        //如果header已经显示，则执行动画隐藏header
                        topViewAnim(1);
                    }
                } else if (startY - endY > moveLen) {
                    direction = 1;//向head上滑动
                    if (topViewShow) break;//如果header已经显示则不做处理
                    else {
                        //如果没显示，则执行动画显示header
                        topViewAnim(0);
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private ObjectAnimator objectAnimator;

    private void topViewAnim(int flag) {
        if (objectAnimator != null && objectAnimator.isRunning())
            objectAnimator.cancel();
        if (flag == 0)//向下滑动，显示TopView
        {
            objectAnimator.ofFloat(topView, "translationY", topView.getTranslationY(), -topView.getHeight());
        } else {
            //向上滑动,显示TopView
            objectAnimator.ofFloat(topView, "translationY", topView.getTranslationY(), 0);
        }
    }
}
