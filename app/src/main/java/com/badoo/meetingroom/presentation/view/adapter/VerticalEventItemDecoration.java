package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.meetingroom.presentation.model.intf.EventModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyaozhong on 20/01/2017.
 */

public class VerticalEventItemDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        if (!mDividerList.get(parent.getChildAdapterPosition(view))) {
            return;
        }

        outRect.bottom = mDivider.getIntrinsicHeight();
    }

    private Drawable mDivider;
    private List<Boolean> mDividerList;


    /**
     * Custom divider will be used
     */
    public VerticalEventItemDecoration(Context context, int resId, List<EventModel> roomEventModelList) {
        mDivider = ContextCompat.getDrawable(context, resId);
        mDividerList = new ArrayList<>();

        for (int i = 0; i < roomEventModelList.size() - 1; i++) {

            EventModel currentEvent = roomEventModelList.get(i);
            EventModel nextEvent = roomEventModelList.get(i + 1);

            if (currentEvent.getStatus() == nextEvent.getStatus() && currentEvent.isAvailable()) {
                mDividerList.add(false);
            } else {
                mDividerList.add(true);
            }
        }
        mDividerList.add(false);
    }
}
