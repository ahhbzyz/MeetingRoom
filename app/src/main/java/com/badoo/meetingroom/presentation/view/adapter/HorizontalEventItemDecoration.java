package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangyaozhong on 21/01/2017.
 */

public class HorizontalEventItemDecoration extends RecyclerView.ItemDecoration{

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        outRect.right = mDivider.getIntrinsicHeight();
    }

    private Drawable mDivider;

    /**
     * Custom divider will be used
     */
    public HorizontalEventItemDecoration(Context context, int resId) {
        mDivider = ContextCompat.getDrawable(context, resId);
    }
}
