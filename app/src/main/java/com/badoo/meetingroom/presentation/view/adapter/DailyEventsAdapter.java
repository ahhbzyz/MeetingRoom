package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
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
import com.badoo.meetingroom.presentation.model.Room;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.model.RoomEventModelImpl;
import com.badoo.meetingroom.presentation.view.component.drawable.BusyBgDrawable;
import com.badoo.meetingroom.presentation.view.component.drawable.TimelineBarDrawable;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.ViewHolder> {

    private static final int MIN_SLOT_TIME = 5;
    private static final float HEIGHT_PER_MILLIS =  38f / TimeHelper.min2Millis(MIN_SLOT_TIME);

    private final Context mContext;
    private List<RoomEventModel> mEvents;
    private OnItemClickListener mOnItemClickListener;
    private SparseIntArray mBottomTimelineBarHeights;
    private int mPage = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_event_period) TextView mEventPeriodTv;
        @BindView(R.id.tv_event_info) TextView mEventInfoTv;
        @BindView(R.id.img_timeline_bar) ImageView mTimelineBar;
        @BindView(R.id.layout_event_content) LinearLayout mEventContentLayout;
        @BindView(R.id.layout_timestamp) RelativeLayout mTimestampLayout;
        @BindView(R.id.layout_clickable) FrameLayout mClickableLayout;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    DailyEventsAdapter(Context context) {
        mContext = context;
        mEvents = new ArrayList<>();
        mBottomTimelineBarHeights = new SparseIntArray();
    }

    public void setDailyEventList(List<RoomEventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        this.mEvents = roomEventModelList;
        if (!mEvents.isEmpty()) {
            RoomEventModel fakeEvent = new RoomEventModelImpl();
            fakeEvent.setStatus(RoomEventModel.AVAILABLE);
            fakeEvent.setStartTime(mEvents.get(mEvents.size() - 1).getEndTime());
            fakeEvent.setEndTime(fakeEvent.getStartTime() + TimeHelper.min2Millis(10));
            mEvents.add(fakeEvent);
        }
        this.notifyDataSetChanged();
    }

    public void setPage(int page) {
        mPage = page;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_vertical_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,  int position) {



        // Current event
        RoomEventModel event = mEvents.get(position);

        // Remaining progress of event
        float remainingProgress
            = event.getRemainingTime() / (float)event.getDuration();

        // Calculate item height
        float viewHeight =  event.getDuration() * HEIGHT_PER_MILLIS + mContext.getResources().getDimension(R.dimen.daily_events_divider_height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) viewHeight);
        holder.itemView.setLayoutParams(params);


        // Add timestamps to item view
        holder.mTimestampLayout.removeAllViews();
        List<Long> timestamps = calcTimeStampsInItemView(event);
        addTimestampsToView(holder, position, timestamps, event, viewHeight, remainingProgress);


        if (position == getItemCount() - 1) {
            holder.mEventPeriodTv.setText("");
            holder.mEventInfoTv.setText("");
            holder.mTimelineBar.setBackground(null);
            holder.mEventContentLayout.setBackground(null);
            return;
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

        // new item layout

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
            if (event.isBusy()) {
                TimelineBarDrawable barDrawable = new TimelineBarDrawable(event.getEventExpiredColor(), event.getBusyColor(), remainingProgress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventInfoTv.setText(event.getOrganizer());
                holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE, remainingProgress);
                holder.mEventContentLayout.setBackground(bg);
                holder.mEventPeriodTv.setText(event.getDurationInText());
            } else {

                if (mBottomTimelineBarHeights.get(position) < holder.mEventPeriodTv.getMeasuredHeight()) {

                    holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);

                } else if (mBottomTimelineBarHeights.get(position) < (holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventInfoTv.getMeasuredHeight())) {

                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);

                }

                int topTimelineBarHeight = (int) (viewHeight * (1 - remainingProgress));
                offsetParams.setMargins(0, topTimelineBarHeight, 0, 0);
                holder.mEventPeriodTv.setLayoutParams(offsetParams);

                TimelineBarDrawable barDrawable = new TimelineBarDrawable(event.getEventExpiredColor(), event.getAvailableColor(), remainingProgress);

                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventInfoTv.setText(mContext.getString(R.string.available));
                holder.mEventInfoTv.setTextColor(event.getAvailableColor());
                holder.mEventPeriodTv.setText(TimeHelper.getCurrentTimeInMillisInText() + " - " + event.getEndTimeInText());
                BusyBgDrawable bg = new BusyBgDrawable(Color.TRANSPARENT, Color.TRANSPARENT, remainingProgress
                );
                holder.mEventContentLayout.setBackground(bg);
            }
        }

        // Event is coming
        if (event.isComing()){

            if (event.isAvailable()) {
                holder.mTimelineBar.setBackgroundColor(event.getAvailableColor());
                holder.mEventInfoTv.setText(mContext.getString(R.string.available));
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

        if (event.isAvailable() && !event.isExpired()) {
            int finalPosition = position;
            holder.mClickableLayout.setOnClickListener(v -> {
                if (this.mOnItemClickListener != null) {
                    this.mOnItemClickListener.onEventItemClicked(holder.itemView, finalPosition);
                }
            });
        } else {
            holder.mClickableLayout.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public float getHeightPerMillis() {
        return HEIGHT_PER_MILLIS;
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEventItemClicked(View view, int position);
    }

    private void addTimestampsToView(ViewHolder holder, int position, List<Long> timestamps, RoomEventModel event, float viewHeight, float remainingProgress) {
        TextView fakeTv = new TextView(mContext);
        fakeTv.setText(mContext.getString(R.string.fake_time));
        fakeTv.setPadding(0, 0, 32, 0);
        fakeTv.setVisibility(View.INVISIBLE);
        holder.mTimestampLayout.addView(fakeTv);

        int bottomTimelineBarHeight = (int) (viewHeight * remainingProgress);
        mBottomTimelineBarHeights.put(position, bottomTimelineBarHeight);

        for (int i = 0; i < timestamps.size(); i++) {

            long ts = timestamps.get(i);

            TextView timestampTv = new TextView(mContext);
            timestampTv.setText(TimeHelper.formatTime(ts));
            timestampTv.setPadding(0, 0, 32, 0);
            timestampTv.measure(0, 0);

            float textOffset;
            float textViewHeight = timestampTv.getMeasuredHeight();

            if (ts == Badoo.getStartTimeOfDay(mPage)) {

                textOffset = 0;

            } else {

                textOffset = textViewHeight / 2f;
            }

            float topMargin = (ts - event.getStartTime()) * HEIGHT_PER_MILLIS - textOffset;
            timestampTv.setY(topMargin);

            if (TimeHelper.getCurrentTimeInMillis() >= ts - TimeHelper.min2Millis(3) &&
                TimeHelper.getCurrentTimeInMillis() <= ts + TimeHelper.min2Millis(3)) {
                continue;
            }

            holder.mTimestampLayout.addView(timestampTv);
        }
    }


    private List<Long> calcTimeStampsInItemView(RoomEventModel event){

        List<Long> result = new ArrayList<>();

        float hour = TimeHelper.getHour(event.getStartTime());

        while (true) {
            long timestamp = TimeHelper.getMidNightTimeOfDay(mPage) + TimeHelper.min2Millis((int) (hour * 30));
            hour += 0.5f;
            if (timestamp >= event.getStartTime() && timestamp < event.getEndTime()) {
                result.add(timestamp);
            }
            if (timestamp > event.getEndTime()) {
                break;
            }
        }
        return result;
    }
}
