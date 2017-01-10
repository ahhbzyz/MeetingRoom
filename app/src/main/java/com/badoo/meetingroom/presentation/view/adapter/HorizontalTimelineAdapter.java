package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.component.drawable.TimelineBarDrawable;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 08/01/2017.
 */

public class HorizontalTimelineAdapter extends RecyclerView.Adapter<HorizontalTimelineAdapter.ViewHolder>{

    private static final float WIDTH_PER_MILLIS =  38f / TimeHelper.min2Millis(DailyEventsFragment.MIN_SLOT_TIME);

    private List<RoomEventModel> mEvents;
    private float mDividerWidth;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_start_time) TextView mStartTimeTv;
        @BindView(R.id.img_timeline_bar) ImageView mTimelineBarImg;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    HorizontalTimelineAdapter(Context context) {
        mEvents = new ArrayList<>();
        mDividerWidth = context.getResources().getDimension(R.dimen.horizontal_timeline_divider_width);
    }

    public void setEventList(List<RoomEventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        mEvents = roomEventModelList;
        notifyDataSetChanged();
        // TODO Item change
        //notifyItemChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_horizontal_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomEventModel event = mEvents.get(position);
        float restProgress = event.getRemainingTime() / (float)event.getDuration();
        float viewWidth = event.getDuration() * WIDTH_PER_MILLIS + mDividerWidth;
        RelativeLayout.LayoutParams params = new
            RelativeLayout.LayoutParams(Math.round(viewWidth), RelativeLayout.LayoutParams.MATCH_PARENT);
        holder.itemView.setLayoutParams(params);
        holder.mStartTimeTv.setText(event.getStartTimeInText());
        holder.mStartTimeTv.measure(0, 0);
        if (viewWidth >= holder.mStartTimeTv.getMeasuredWidth()) {
            holder.mStartTimeTv.setVisibility(View.VISIBLE);
        } else {
            holder.mStartTimeTv.setVisibility(View.INVISIBLE);
        }
        TimelineBarDrawable barDrawable
            = new TimelineBarDrawable(event.getEventExpiredColor(), event.getEventColor(), restProgress);
        barDrawable.setOrientation(TimelineBarDrawable.HORIZONTAL);
        holder.mTimelineBarImg.setBackground(barDrawable);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public float getWidthPerMillis() {
        return WIDTH_PER_MILLIS;
    }

}
