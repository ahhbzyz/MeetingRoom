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
    private static final int MIN_SLOT_TIME = 5;
    private static float WIDTH_PER_MILLIS = (70 + 10) / (float)TimeHelper.min2Millis(MIN_SLOT_TIME);
    private List<RoomEventModel> mEvents;
    private float mDividerWidth;
    private OnItemClickListener mOnItemClickListener;

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
        mDividerWidth = context.getResources().getDimension(R.dimen.horizontal_timeline_time_slot_divider_width);
    }

    public void setEventList(List<RoomEventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        mEvents = roomEventModelList;
        notifyDataSetChanged();
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
        if (viewWidth >= holder.mStartTimeTv.getMeasuredWidth() + 5) {
            holder.mStartTimeTv.setVisibility(View.VISIBLE);
        } else {
            holder.mStartTimeTv.setVisibility(View.INVISIBLE);
        }
        TimelineBarDrawable barDrawable
            = new TimelineBarDrawable(event.getEventExpiredColor(), event.getEventColor(), restProgress);
        barDrawable.setOrientation(TimelineBarDrawable.HORIZONTAL);
        holder.mTimelineBarImg.setBackground(barDrawable);
        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onEventItemClicked(position));
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public float getWidthPerMillis() {
        return WIDTH_PER_MILLIS;
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEventItemClicked(int position);
    }
}
