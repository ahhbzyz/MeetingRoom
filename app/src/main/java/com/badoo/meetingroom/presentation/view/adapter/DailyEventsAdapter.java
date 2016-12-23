//package com.badoo.meetingroom.presentation.view.adapter;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.badoo.meetingroom.domain.RoomEvent;
//import com.badoo.meetingroom.R;
//import com.badoo.meetingroom.Status;
//import com.badoo.meetingroom.presentation.view.component.customdrawable.BusyBgDrawable;
//import com.badoo.meetingroom.presentation.view.component.customdrawable.TimelineBarDrawable;
//import com.badoo.meetingroom.utils.Utils;
//
//import java.util.List;
//
///**
// * Created by zhangyaozhong on 19/12/2016.
// */
//
//public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.ViewHolder> {
//    private float mWidthTimeRatio;
//    private List<RoomEvent> mEvents;
//    private Context mContext;
//    private RelativeLayout.LayoutParams params;
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView mTvStartTime, mTvEventTime, mTvStatus;
//        public ImageView mImgTimelineBar;
//        public LinearLayout mLayoutEventContent;
//        public View mItemDivider;
//
//        public ViewHolder(View view) {
//            super(view);
//            mTvStartTime = (TextView)view.findViewById(R.id.tv_start_time);
//            mTvEventTime = (TextView)view.findViewById(R.id.tv_event_time);
//            mTvStatus = (TextView)view.findViewById(R.id.tv_status);
//            mImgTimelineBar = (ImageView)view.findViewById(R.id.img_timeline_bar);
//            mLayoutEventContent = (LinearLayout)view.findViewById(R.id.layout_event_content);
//            mItemDivider = view.findViewById(R.id.view_item_divider);
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public DailyEventsAdapter(Context context, List<RoomEvent> events) {
//        mContext = context;
//        mEvents = events;
//        mWidthTimeRatio = 300f / (60 * 60 * 1000);
//        // Set item height
//
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext())
//            .inflate(R.layout.item_event, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//
//        RoomEvent event = mEvents.get(position);
//
//        float progress = event.getLeftTime() / (float)event.getDuration();
//        int [] colors = {Status.EXPIRED_COLOR, event.getStatus() == Status.AVAILABLE ? Status.AVAILABLE_COLOR:Status.BUSY_COLOR};
//        TimelineBarDrawable barDrawable = new TimelineBarDrawable(colors, progress);
//
//
//        params = new
//            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//            RelativeLayout.LayoutParams.MATCH_PARENT);
//        params.height = (int)(event.getDuration() * mWidthTimeRatio);
//        holder.itemView.setLayoutParams(params);
//
//        holder.mTvStartTime.setTextColor(Color.GRAY);
//        holder.mTvStatus.setTextColor(Color.GRAY);
//        holder.mLayoutEventContent.setBackground(null);
//        holder.mImgTimelineBar.setBackground(null);
//
//        holder.mTvStartTime.setText(Utils.formatTime(mEvents.get(position).getStartTime()));
//        holder.mTvEventTime.setText(event.getPeriod());
//
//        // Event Expired
//        if (event.isExpired()) {
//
//            holder.mImgTimelineBar.setBackgroundColor(Status.EXPIRED_COLOR);
//
//            if (event.getStatus() == Status.AVAILABLE) {
//                holder.mTvStatus.setText("");
//            } else {
//                holder.mTvStatus.setText(event.getOrganizer());
//            }
//        }
//
//        // Event in processing
//        if (event.isProcessing()) {
//
//            holder.mTvStatus.setText(event.getOrganizer());
//
//            holder.mImgTimelineBar.setBackground(barDrawable);
//
//            if (event.getStatus() == Status.BUSY) {
//                holder.mLayoutEventContent.setBackground(null);
//            } else {
//                holder.mLayoutEventContent.setBackground(null);
//            }
//
//            BusyBgDrawable bg = new BusyBgDrawable(Status.EXPIRED_COLOR_OPACITIY_30, Status.BUSY_BG_COLOR, Color.WHITE, progress);
//            holder.mLayoutEventContent.setBackground(bg);
//        }
//
//        // Event is coming
//        if (event.isComing()){
//
//
//            if (event.getStatus() == Status.AVAILABLE) {
//
//                holder.mImgTimelineBar.setBackgroundColor(Status.AVAILABLE_COLOR);
//
//                holder.mTvStatus.setText("Available");
//                holder.mTvStatus.setTextColor(Status.AVAILABLE_COLOR);
//
//
//                holder.mLayoutEventContent.setBackground(null);
//            } else {
//
//                holder.mImgTimelineBar.setBackgroundColor(Status.BUSY_COLOR);
//
//                holder.mTvStatus.setText(event.getOrganizer());
//                holder.mTvStatus.setTextColor(Color.GRAY);
//
//                BusyBgDrawable bg = new BusyBgDrawable(Status.BUSY_BG_COLOR, Color.WHITE);
//                holder.mLayoutEventContent.setBackground(bg);
//            }
//        }
//        else {
//
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mEvents.size();
//    }
//}
