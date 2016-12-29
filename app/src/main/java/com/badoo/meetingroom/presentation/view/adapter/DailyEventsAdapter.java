package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private RelativeLayout.LayoutParams params;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvStartTime, mTvEventTime, mTvStatus;
        private ImageView mTimelineBar;
        private LinearLayout mLayoutEventContent;
        private ViewHolder(View view) {
            super(view);
            mTvStartTime = (TextView)view.findViewById(R.id.tv_start_time);
            mTvEventTime = (TextView)view.findViewById(R.id.tv_event_time);
            mTvStatus = (TextView)view.findViewById(R.id.tv_status);
            mTimelineBar = (ImageView)view.findViewById(R.id.img_timeline_bar);
            mLayoutEventContent = (LinearLayout)view.findViewById(R.id.layout_event_content);
        }
    }

    @Inject
    DailyEventsAdapter(Context context) {
        mContext = context;
        mEvents = new ArrayList<>();
        mWidthTimeRatio = 300f / (60 * 60 * 1000);
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



        params = new
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            (int)(event.getDuration() * mWidthTimeRatio));


        holder.itemView.setLayoutParams(params);

        holder.mTvStartTime.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        holder.mTvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        holder.mLayoutEventContent.setBackground(null);
        holder.mTimelineBar.setBackground(null);

        holder.mTvStartTime.setText(event.getStartTimeInText());
        holder.mTvEventTime.setText(event.getPeriod());

        // Event Expired
        if (event.isExpired()) {

            holder.mTimelineBar.setBackgroundColor(event.getEventExpiredColor());

            if (event.isAvailable()) {
                holder.mTvStatus.setText("");
            } else {
                holder.mTvStatus.setText(event.getOrganizer());
            }
        }

        // Event in processing
        if (event.isProcessing()) {
            float progress = event.getRemainingTime() / (float)event.getDuration();


            if (event.isBusy()) {
                TimelineBarDrawable barDrawable
                    = new TimelineBarDrawable(event.getEventExpiredColor(), event.getBusyColor(), progress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mTvStatus.setText(event.getOrganizer());
                holder.mLayoutEventContent.setBackground(null);
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), progress);
                holder.mLayoutEventContent.setBackground(bg);
            } else {
                TimelineBarDrawable barDrawable
                    = new TimelineBarDrawable(event.getEventExpiredColor(), event.getAvailableColor(), progress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mTvStatus.setText("");
                holder.mLayoutEventContent.setBackground(null);
            }


        }

        // Event is coming
        if (event.isComing()){

            if (event.isAvailable()) {
                holder.mTimelineBar.setBackgroundColor(event.getAvailableColor());
                holder.mTvStatus.setText("Available");
                holder.mTvStatus.setTextColor(event.getAvailableColor());
                holder.mLayoutEventContent.setBackground(null);
            } else {
                holder.mTimelineBar.setBackgroundColor(event.getBusyColor());
                holder.mTvStatus.setText(event.getOrganizer());
                holder.mTvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor());
                holder.mLayoutEventContent.setBackground(bg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
