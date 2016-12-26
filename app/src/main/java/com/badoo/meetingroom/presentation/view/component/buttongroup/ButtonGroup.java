package com.badoo.meetingroom.presentation.view.component.buttongroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangyaozhong on 15/12/2016.
 */

public class ButtonGroup extends ViewGroup {

    public ButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int count = getChildCount();
        int width = 0;
        int height = 0;
        MarginLayoutParams params = null;
        for (int i = 0 ;i < count; i++) {
            View childView = getChildAt(0);
            width = childView.getMeasuredWidth();
            height = childView.getMeasuredHeight();
            params = (MarginLayoutParams) childView.getLayoutParams();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        final int count = getChildCount();
    }
}
