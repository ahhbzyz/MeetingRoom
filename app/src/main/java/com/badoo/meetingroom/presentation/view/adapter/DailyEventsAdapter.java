package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.EventModel;
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
    private static final float HEIGHT_PER_MILLIS = 38f / TimeHelper.min2Millis(MIN_SLOT_TIME);

    private final Context mContext;
    private ArrayList<EventModel> mEventModelList;
    private OnItemClickListener mOnItemClickListener;
    private View mLastDividerView;
    private SparseArray<List<TextView>> mTimestampTextViews;
    private TextView mHiddenTv;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_event_period)
        TextView mEventPeriodTv;
        @BindView(R.id.tv_event_info)
        TextView mEventInfoTv;
        @BindView(R.id.img_timeline_bar)
        ImageView mTimelineBar;
        @BindView(R.id.layout_event_content)
        LinearLayout mEventContentLayout;
        @BindView(R.id.layout_timestamps)
        RelativeLayout mTimestampsLayout;
        @BindView(R.id.layout_clickable)
        FrameLayout mClickableLayout;
        @BindView(R.id.layout_current_time)
        LinearLayout mCurrentTimeLayout;
        @BindView(R.id.tv_current_time)
        TextView mCurrentTimeTv;
        @BindView(R.id.layout_no_timestamps)
        RelativeLayout mNoTimestampsLayout;
        @BindView(R.id.view_item_divider_fill)
        View mItemDividerFillView;


        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    DailyEventsAdapter(Context context) {
        mContext = context;
        mEventModelList = new ArrayList<>();
        mTimestampTextViews = new SparseArray<>();
    }

    public void setDailyEventList(List<EventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        mEventModelList = (ArrayList<EventModel>) roomEventModelList;
        notifyDataSetChanged();

        mLastDividerView = new View(mContext);
        mLastDividerView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_divider_color));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            (int) mContext.getResources().getDimension(R.dimen.item_vertical_event_divider_height));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mLastDividerView.setLayoutParams(params);

        mHiddenTv = new TextView(mContext);
        mHiddenTv.setText(mContext.getString(R.string.fake_time));
        mHiddenTv.setVisibility(View.INVISIBLE);

        createTimestampTextViews(mEventModelList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_vertical_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Current event
        EventModel event = mEventModelList.get(position);


        // Remaining progress of event
        float remainingProgress
            = event.getRemainingTime() / (float) event.getDuration();

        // Calculate item height
        float viewHeight = event.getDuration() * HEIGHT_PER_MILLIS + mContext.getResources().getDimension(R.dimen.item_vertical_event_divider_height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) viewHeight);
        holder.itemView.setLayoutParams(params);

        holder.mEventPeriodTv.measure(0, 0);
        holder.mEventInfoTv.measure(0, 0);

        holder.mEventPeriodTv.setVisibility(View.VISIBLE);
        holder.mEventInfoTv.setVisibility(View.VISIBLE);

        if (viewHeight < holder.mEventPeriodTv.getMeasuredHeight()) {
            holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
            holder.mEventInfoTv.setVisibility(View.INVISIBLE);
        }
        else if (viewHeight < (holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventInfoTv.getMeasuredHeight())) {
            holder.mEventInfoTv.setVisibility(View.INVISIBLE);
        }

        // new item layout
        holder.mEventPeriodTv.setText("");
        holder.mEventInfoTv.setText("");


        holder.mItemDividerFillView.setBackgroundColor(event.getEventColor());


        // Reset view
        LinearLayout.LayoutParams offsetParams
            = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        offsetParams.setMargins(0, 0, 0, 0);
        holder.mEventPeriodTv.setLayoutParams(offsetParams);

        // Event Expired
        if (event.isExpired()) {
            if (event.isAvailable()) {
                holder.mEventInfoTv.setText("");
            }
            else {
                holder.mEventPeriodTv.setText(event.getDurationInText());
                holder.mEventInfoTv.setText(event.getCreatorEmailAddress());
            }

            holder.mTimelineBar.setBackgroundColor(event.getEventExpiredColor());
            holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
            holder.mEventContentLayout.setBackground(null);
            holder.mItemDividerFillView.setBackgroundColor(event.getEventExpiredColor());

        }

        float topTimelineBarHeight = event.getDuration() * HEIGHT_PER_MILLIS * (1 - remainingProgress);
        float bottomTimelineBarHeight = event.getDuration() * HEIGHT_PER_MILLIS * remainingProgress;


        if (event.isProcessing()) {
            holder.mCurrentTimeLayout.setVisibility(View.VISIBLE);
            holder.mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
            holder.mCurrentTimeLayout.measure(0, 0);
            holder.mCurrentTimeLayout.setY(topTimelineBarHeight
                - holder.mCurrentTimeLayout.getMeasuredHeight() / 2f
                + mContext.getResources().getDimension(R.dimen.item_vertical_event_divider_height));
        }
        else {
            holder.mCurrentTimeLayout.setVisibility(View.GONE);
        }

        // Event in processing
        if (event.isProcessing()) {

            holder.mItemDividerFillView.setBackgroundColor(event.getEventExpiredColor());

            if (event.isAvailable()) {
                if (bottomTimelineBarHeight < holder.mEventPeriodTv.getMeasuredHeight()) {
                    holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);
                }
                else if (bottomTimelineBarHeight < (holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventInfoTv.getMeasuredHeight())) {
                    holder.mEventInfoTv.setVisibility(View.INVISIBLE);
                }

                TimelineBarDrawable barDrawable = new TimelineBarDrawable(event.getEventExpiredColor(), event.getAvailableColor(), remainingProgress);

                holder.mTimelineBar.setBackground(barDrawable);
                BusyBgDrawable bg = new BusyBgDrawable(Color.TRANSPARENT, Color.TRANSPARENT, remainingProgress);
                holder.mEventContentLayout.setBackground(bg);
            }
            else {
                TimelineBarDrawable barDrawable = new TimelineBarDrawable(event.getEventExpiredColor(), event.getBusyColor(), remainingProgress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventPeriodTv.setText(event.getDurationInText());
                holder.mEventInfoTv.setText(event.getCreatorEmailAddress());
                holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE, remainingProgress);
                holder.mEventContentLayout.setBackground(bg);
                holder.mEventPeriodTv.setText(event.getDurationInText());
            }
        }

        // Event is coming
        if (event.isComing()) {
            if (event.isAvailable()) {
                holder.mTimelineBar.setBackgroundColor(event.getAvailableColor());
                holder.mEventContentLayout.setBackground(null);
            }
            else {
                holder.mTimelineBar.setBackgroundColor(event.getBusyColor());
                holder.mEventPeriodTv.setText(event.getDurationInText());
                holder.mEventInfoTv.setText(event.getCreatorEmailAddress());
                holder.mEventInfoTv.setTextColor(ContextCompat.getColor(mContext, R.color.textGray));
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE);
                holder.mEventContentLayout.setBackground(bg);
            }
        }


        holder.mTimestampsLayout.removeAllViews();


        if (mTimestampTextViews.get(position) != null) {

            for (int i = 0 ; i < mTimestampTextViews.get(position).size(); i ++) {
                TextView view  = mTimestampTextViews.get(position).get(i);
                if (TimeHelper.getCurrentTimeInMillis() >= event.getTimeStamps().get(i) - TimeHelper.min2Millis(3) &&
                    TimeHelper.getCurrentTimeInMillis() <= event.getTimeStamps().get(i) + TimeHelper.min2Millis(3)) {
                    continue;
                }
                if(view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                holder.mTimestampsLayout.addView(view);
            }
        }

        if (holder.mTimestampsLayout.getChildCount() == 0) {
            if(mHiddenTv.getParent() != null) {
                ((ViewGroup) mHiddenTv.getParent()).removeView(mHiddenTv);
            }
            holder.mTimestampsLayout.addView(mHiddenTv);
        }

        addDividerToEnd(holder, position);

        if (event.isAvailable() && !event.isExpired()) {
            holder.mClickableLayout.setEnabled(true);
            holder.mClickableLayout.setOnClickListener(v -> {
                if (this.mOnItemClickListener != null) {
                    this.mOnItemClickListener.onEventItemClicked(position, mEventModelList);
                }
            });
        }
        else {
            holder.mClickableLayout.setEnabled(false);
            holder.mClickableLayout.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mEventModelList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEventItemClicked(int position, ArrayList<EventModel> eventModelList);
    }

    private void createTimestampTextViews(List<EventModel> eventModelList) {


        for (int i = 0; i < eventModelList.size(); i ++) {

            EventModel eventModel = eventModelList.get(i);

            List<TextView> textViewList = new ArrayList<>();

            for (int j = 0; j < eventModel.getTimeStamps().size(); j++) {

                long ts = eventModel.getTimeStamps().get(j);

                TextView timestampTv = new TextView(mContext);
                timestampTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_timestamp_color));
                timestampTv.setText(TimeHelper.formatTime(ts));

                timestampTv.measure(0, 0);

                float textViewHeight = timestampTv.getMeasuredHeight();

                float textOffset = textViewHeight / 2f;

                float topMargin = (ts - eventModel.getStartTime()) * HEIGHT_PER_MILLIS
                    - textOffset
                    + mContext.getResources().getDimension(R.dimen.item_vertical_event_divider_height);

                timestampTv.setY(topMargin);

                textViewList.add(timestampTv);
            }
            mTimestampTextViews.put(i, textViewList);
        }
    }

    private void addDividerToEnd(ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            if(mLastDividerView.getParent() != null) {
                ((ViewGroup) mLastDividerView.getParent()).removeView(mLastDividerView);
            }
            holder.mNoTimestampsLayout.addView(mLastDividerView);
        } else {
            holder.mNoTimestampsLayout.removeView(mLastDividerView);
        }
    }
}
