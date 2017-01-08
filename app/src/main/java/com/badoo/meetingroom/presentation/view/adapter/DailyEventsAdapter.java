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
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.component.drawable.BusyBgDrawable;
import com.badoo.meetingroom.presentation.view.component.drawable.TimelineBarDrawable;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.ViewHolder> {

    static final float WIDTH_PER_MILLIS =  38f / TimeHelper.min2Millis(DailyEventsFragment.MIN_SLOT_TIME);

    private List<RoomEventModel> mEvents;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private final int mPage;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_event_period) TextView mEventPeriodTv;
        @BindView(R.id.tv_event_info) TextView mEventInfoTv;
        @BindView(R.id.img_timeline_bar) ImageView mTimelineBar;
        @BindView(R.id.layout_event_content) LinearLayout mEventContentLayout;
        @BindView(R.id.layout_event_content_parent) FrameLayout mEventContentParentLayout;
        @BindView(R.id.layout_timestamp) RelativeLayout mTimestampLayout;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mEventPeriodTv.measure(0, 0);
        }
    }

    @Inject
    public DailyEventsAdapter(Context context, int page) {
        mContext = context;
        mPage = page;
        mEvents = new ArrayList<>();
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

    private List<Long> getAllTimeStampsInView(RoomEventModel event){

        List<Long> result = new ArrayList<>();

        int hour = TimeHelper.getHour(event.getStartTime());

        while (true) {
            long timestamp = TimeHelper.getMidNightTimeOfDay(mPage) + TimeHelper.hr2Millis(hour++);
            if (timestamp >= event.getStartTime() && timestamp <= event.getEndTime()) {
                result.add(timestamp);
            }
            if (timestamp > event.getEndTime()) {
                break;
            }
        }

        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,  int position) {

        // Current position
        position = holder.getAdapterPosition();

        // Current event
        RoomEventModel event = mEvents.get(position);

        // Calculate item height
        int viewHeight =  (int) (event.getDuration() * WIDTH_PER_MILLIS);

        List<Long> timestamps = getAllTimeStampsInView(event);

        holder.mTimestampLayout.removeAllViews();

        if (timestamps.isEmpty()) {
            TextView timestampTv = new TextView(mContext);
            timestampTv.setText("00:00");
            timestampTv.setPadding(0, 0, 32, 0);
            timestampTv.setVisibility(View.INVISIBLE);
            holder.mTimestampLayout.addView(timestampTv);
        }

        for (int i = 0; i < timestamps.size(); i++) {
            long ts = timestamps.get(i);
            TextView timestampTv = new TextView(mContext);
            timestampTv.setText(TimeHelper.formatTime(ts));
            timestampTv.setPadding(0, 0, 32, 0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            timestampTv.measure(0, 0);
            float textOffset;
            if (ts == Badoo.getStartTimeOfDay(mPage)) {
                textOffset = 0;
            } else if (ts == Badoo.getEndTimeOfDay(mPage)) {
                textOffset = timestampTv.getMeasuredHeight();
            } else {
                textOffset = timestampTv.getMeasuredHeight() / 2f;
            }
            float topMargin = (ts - event.getStartTime()) * WIDTH_PER_MILLIS - textOffset;
            params.setMargins(0, (int) topMargin, 0, 0);
            timestampTv.setLayoutParams(params);
            holder.mTimestampLayout.addView(timestampTv);
        }

        holder.mEventPeriodTv.measure(0, 0);
        holder.mEventInfoTv.measure(0, 0);

        holder.mEventPeriodTv.setVisibility(View.VISIBLE);
        holder.mEventInfoTv.setVisibility(View.VISIBLE);

        if (viewHeight < holder.mEventPeriodTv.getMeasuredHeight()) {
            holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
            holder.mEventInfoTv.setVisibility(View.INVISIBLE);
        } else if (viewHeight < (holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventInfoTv.getMeasuredHeight())) {
            holder.mEventInfoTv.setVisibility(View.INVISIBLE);
        }

        // Remaining progress of event
        float restProgress = event.getRemainingTime() / (float)event.getDuration();

        // new item layout
        RelativeLayout.LayoutParams params = new
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, viewHeight);
        holder.itemView.setLayoutParams(params);

        holder.mEventPeriodTv.setText(event.getDurationInText());
        holder.mEventPeriodTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));

        // Reset view
        LinearLayout.LayoutParams offsetParams
            = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        offsetParams.setMargins(0, 0, 0, 0);
        holder.mEventPeriodTv.setLayoutParams(offsetParams);

        // Event Expired
        if (event.isExpired()) {
            if (event.isAvailable()) {
                holder.mEventInfoTv.setText("");
            } else {
                holder.mEventInfoTv.setText(event.getOrganizer());
            }

            holder.mTimelineBar.setBackgroundColor(event.getEventExpiredColor());
            holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
            holder.mEventContentLayout.setBackground(null);
        }

        // Event in processing
        if (event.isProcessing()) {

            int mBottomTimelineBarHeight = (int) (viewHeight * restProgress);

            int topTimelineBarHeight = (int) (viewHeight * (1 - restProgress));

            if (event.isBusy()) {
                TimelineBarDrawable barDrawable
                    = new TimelineBarDrawable(event.getEventExpiredColor(), event.getBusyColor(), restProgress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventInfoTv.setText(event.getOrganizer());
                holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE, restProgress);
                holder.mEventContentLayout.setBackground(bg);
                holder.mEventPeriodTv.setText(event.getDurationInText());
            } else {


                if (mBottomTimelineBarHeight < holder.mEventPeriodTv.getMeasuredHeight()) {
                    holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);
                } else if (mBottomTimelineBarHeight < (holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventInfoTv.getMeasuredHeight())) {
                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);
                } else {

                }

                offsetParams.setMargins(0, topTimelineBarHeight, 0, 0);
                holder.mEventPeriodTv.setLayoutParams(offsetParams);

                TimelineBarDrawable barDrawable
                    = new TimelineBarDrawable(event.getEventExpiredColor(), event.getAvailableColor(), restProgress);

                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventInfoTv.setText("Available");
                holder.mEventInfoTv.setTextColor(event.getAvailableColor());
                holder.mEventPeriodTv.setText(TimeHelper.getCurrentTimeInMillisInText() + " - " + event.getEndTimeInText());
                BusyBgDrawable bg = new BusyBgDrawable(Color.TRANSPARENT, Color.TRANSPARENT, restProgress);
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
                holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE);
                holder.mEventContentLayout.setBackground(bg);
            }
        }

        if (event.isAvailable() && !event.isExpired() && event.getRemainingTime() > TimeHelper.min2Millis(DailyEventsFragment.MIN_BOOKING_TIME)) {
            int finalPosition = position;
            holder.mEventContentParentLayout.setOnClickListener(v -> {
                if (this.mOnItemClickListener != null) {
                    this.mOnItemClickListener.onEventItemClicked(finalPosition);
                }
            });
        } else {
            holder.mEventContentParentLayout.setOnClickListener(null);
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

    public float getWidthPerMillis() {
        return WIDTH_PER_MILLIS;
    }
}
