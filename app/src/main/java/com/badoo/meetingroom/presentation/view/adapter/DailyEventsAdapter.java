package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.component.customdrawable.BusyBgDrawable;
import com.badoo.meetingroom.presentation.view.component.customdrawable.TimelineBarDrawable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.ViewHolder> {

    private float mWidthTimeRatio;
    private List<RoomEventModel> mEvents;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStartTimeTv, mEventPeriodTv, mEventInfoTv;
        private ImageView mTimelineBar;
        private LinearLayout mEventContentLayout;
        private FrameLayout mEventContentParentLayout;
        private ViewHolder(View view) {
            super(view);
            mStartTimeTv = (TextView)view.findViewById(R.id.tv_start_time);
            mEventPeriodTv = (TextView)view.findViewById(R.id.tv_event_period);
            mEventInfoTv = (TextView)view.findViewById(R.id.tv_event_info);
            mTimelineBar = (ImageView)view.findViewById(R.id.img_timeline_bar);
            mEventContentLayout = (LinearLayout)view.findViewById(R.id.layout_event_content);
            mEventContentParentLayout  = (FrameLayout)view.findViewById(R.id.layout_event_content_parent);
        }
    }

    @Inject
    public DailyEventsAdapter(Context context, float widthTimeRatio) {
        mContext = context;
        mEvents = new ArrayList<>();
        mWidthTimeRatio = widthTimeRatio;
    }

    public void setDailyEventList(List<RoomEventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        this.mEvents = roomEventModelList;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RoomEventModel event = mEvents.get(position);
        int viewHeight =  (int) (event.getDuration() * mWidthTimeRatio);
        float progress = event.getRemainingTime() / (float)event.getDuration();
        int bottomHeight = (int) (viewHeight * progress);
        int topHeight = (int) (viewHeight * (1 - progress));


        RelativeLayout.LayoutParams params = new
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, viewHeight);
        holder.itemView.setLayoutParams(params);


        holder.mStartTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        holder.mStartTimeTv.setText(event.getStartTimeInText());

        LinearLayout.LayoutParams offsetParams
            = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        offsetParams.setMargins(0, 0, 0, 0);
        holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        holder.mEventPeriodTv.setText(event.getPeriod());
        holder.mEventPeriodTv.setLayoutParams(offsetParams);

        holder.mEventContentLayout.setBackground(null);
        holder.mTimelineBar.setBackground(null);

        // Event Expired
        if (event.isExpired()) {

            holder.mTimelineBar.setBackgroundColor(event.getEventExpiredColor());

            if (event.isAvailable()) {
                holder.mEventInfoTv.setText("");
            } else {
                holder.mEventInfoTv.setText(event.getOrganizer());
            }
        }

        // Event in processing
        if (event.isProcessing()) {
            holder.mStartTimeTv.measure(0, 0);
            if (topHeight < (holder.mStartTimeTv.getMeasuredHeight())) {
                holder.mStartTimeTv.setVisibility(View.INVISIBLE);
            } else {
                holder.mStartTimeTv.setVisibility(View.VISIBLE);
            }

            if (event.isBusy()) {
                TimelineBarDrawable barDrawable
                    = new TimelineBarDrawable(event.getEventExpiredColor(), event.getBusyColor(), progress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventInfoTv.setText(event.getOrganizer());
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE, progress);
                holder.mEventContentLayout.setBackground(bg);

            } else {
                // Text
                holder.mEventPeriodTv.measure(0, 0);
                if (bottomHeight < holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventInfoTv.getMeasuredHeight()) {
                    holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);
                } else {
                    holder.mEventPeriodTv.setVisibility(View.VISIBLE);
                    holder.mEventInfoTv.setVisibility(View.VISIBLE);
                    offsetParams.setMargins(0, topHeight, 0, 0);
                    holder.mEventPeriodTv.setLayoutParams(offsetParams);
                }

                TimelineBarDrawable barDrawable
                    = new TimelineBarDrawable(event.getEventExpiredColor(), event.getAvailableColor(), progress);

                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventInfoTv.setText("Available");
                holder.mEventInfoTv.setTextColor(event.getAvailableColor());
                BusyBgDrawable bg = new BusyBgDrawable(Color.TRANSPARENT, Color.TRANSPARENT, progress);
                holder.mEventContentLayout.setBackground(bg);
            }
        }

        // Event is coming
        if (event.isComing()){

            if (event.isAvailable()) {
                holder.mTimelineBar.setBackgroundColor(event.getAvailableColor());
                holder.mEventInfoTv.setText("Available");
                holder.mEventInfoTv.setTextColor(event.getAvailableColor());
                holder.mEventContentLayout.setBackground(null);
            } else {
                holder.mTimelineBar.setBackgroundColor(event.getBusyColor());
                holder.mEventInfoTv.setText(event.getOrganizer());
                holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE);
                holder.mEventContentLayout.setBackground(bg);
            }
        }

        if (event.isAvailable() && !event.isExpired()) {
            holder.mEventContentParentLayout.setOnClickListener(v -> {
                if (this.mOnItemClickListener != null) {
                    this.mOnItemClickListener.onEventItemClicked(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }


    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEventItemClicked(int position);
    }
}
