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

    private SparseArray<List<TextView>> mTimestampTextViews;
    private SparseArray<List<View>> mDividers;
    private SparseArray<List<FrameLayout>> mSubLayouts;
    private SparseArray<TextView> mHiddenTvs;


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_event_period)
        TextView mEventPeriodTv;
        @BindView(R.id.tv_event_creator)
        TextView mEventCreatorTv;
        @BindView(R.id.tv_event_title)
        TextView mEventTitle;
        @BindView(R.id.img_timeline_bar)
        ImageView mTimelineBar;
        @BindView(R.id.layout_event_text_views)
        LinearLayout mEventTextViewsLayout;
        @BindView(R.id.layout_timestamps)
        RelativeLayout mTimestampsLayout;
        @BindView(R.id.layout_dividers)
        RelativeLayout mDividersLayout;
        @BindView(R.id.layout_current_time)
        LinearLayout mCurrentTimeLayout;
        @BindView(R.id.tv_current_time)
        TextView mCurrentTimeTv;
        @BindView(R.id.view_top_divider)
        View mExpiredEventTopDivider;
        @BindView(R.id.view_bottom_divider)
        View mExpiredEventBottomDivider;

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
        mDividers = new SparseArray<>();
        mHiddenTvs = new SparseArray<>();
        mSubLayouts = new SparseArray<>();
    }

    public void setDailyEventList(List<EventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        mEventModelList = (ArrayList<EventModel>) roomEventModelList;
        notifyDataSetChanged();

        createTimestampTextViewsAndHourlyDivider(mEventModelList);


        for (int i = 0; i < mEventModelList.size(); i++) {
                TextView mHiddenTv = new TextView(mContext);
                mHiddenTv.setText(mContext.getString(R.string.fake_time));
                mHiddenTv.setVisibility(View.INVISIBLE);
                mHiddenTvs.put(i, mHiddenTv);
        }

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
        float viewHeight = event.getDuration() * HEIGHT_PER_MILLIS;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) viewHeight);
        holder.itemView.setLayoutParams(params);

        holder.mEventPeriodTv.measure(0, 0);
        holder.mEventCreatorTv.measure(0, 0);

        holder.mEventPeriodTv.setVisibility(View.VISIBLE);
        holder.mEventCreatorTv.setVisibility(View.VISIBLE);

        if (viewHeight < holder.mEventPeriodTv.getMeasuredHeight()) {
            holder.mEventPeriodTv.setVisibility(View.INVISIBLE);
            holder.mEventCreatorTv.setVisibility(View.INVISIBLE);
        }
        else if (viewHeight < (holder.mEventPeriodTv.getMeasuredHeight() + holder.mEventCreatorTv.getMeasuredHeight())) {
            holder.mEventCreatorTv.setVisibility(View.INVISIBLE);
        }



        float topTimelineBarHeight = event.getDuration() * HEIGHT_PER_MILLIS * (1 - remainingProgress);

        holder.mEventTextViewsLayout.setOrientation(LinearLayout.VERTICAL);

        if (viewHeight < holder.mEventPeriodTv.getMeasuredHeight() * 3 + mContext.getResources().getDimension(R.dimen.item_vertical_event_text_views_top_padding)) {
            holder.mEventTextViewsLayout.setOrientation(LinearLayout.HORIZONTAL);
        }


        // Set Text
        if(event.isFastBooking()) {
            holder.mEventTitle.setText(mContext.getString(R.string.fast_booking));
            holder.mEventCreatorTv.setText(mContext.getString(R.string.no_name_available));
        } else {
            holder.mEventTitle.setText(event.getEventTitle());
            if (event.getCreatorName() != null && event.getCreatorEmailAddress() != null) {
                holder.mEventCreatorTv.setText(event.getCreatorName() + "(" + event.getCreatorEmailAddress() + ")");
            } else {
                holder.mEventCreatorTv.setText(event.getCreatorEmailAddress());
            }
        }

        if (event.isAvailable()) {
            holder.mEventPeriodTv.setText("");
        } else {
            holder.mEventPeriodTv.setText(event.getDurationInText());
        }

        holder.mExpiredEventTopDivider.setVisibility(View.GONE);
        holder.mExpiredEventBottomDivider.setVisibility(View.GONE);

        // Event Expired
        if (event.isExpired()) {


            if (event.isBusy()) {
                holder.mExpiredEventTopDivider.setVisibility(View.VISIBLE);
                holder.mExpiredEventBottomDivider.setVisibility(View.VISIBLE);
                holder.mTimelineBar.setBackgroundColor(event.getEventExpiredColor());
            } else {
                holder.mTimelineBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_expired_time_line_bar));
            }

            holder.mEventTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_title_text_grey));
            holder.mEventCreatorTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_creator_text_grey));

            holder.mEventTextViewsLayout.setBackground(null);
            holder.mCurrentTimeLayout.setVisibility(View.GONE);
        }
        else if (event.isProcessing()) {

            holder.mCurrentTimeLayout.setVisibility(View.VISIBLE);
            holder.mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
            holder.mCurrentTimeLayout.measure(0, 0);
            holder.mCurrentTimeLayout.setY(topTimelineBarHeight - holder.mCurrentTimeLayout.getMeasuredHeight() / 2f);

            holder.mEventTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_title_text));
            holder.mEventCreatorTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_creator_text));


            if (event.isAvailable()) {
                TimelineBarDrawable barDrawable = new TimelineBarDrawable(ContextCompat.getColor(mContext, R.color.item_vertical_event_expired_time_line_bar), event.getAvailableColor(), remainingProgress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventTextViewsLayout.setBackground(null);
            }
            else {
                TimelineBarDrawable barDrawable = new TimelineBarDrawable(event.getEventExpiredColor(), event.getBusyColor(), remainingProgress);
                holder.mTimelineBar.setBackground(barDrawable);
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE, remainingProgress);
                holder.mEventTextViewsLayout.setBackground(bg);
                holder.mEventPeriodTv.setText(event.getDurationInText());
            }
        }
        else {
            holder.mEventTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_title_text));
            holder.mEventCreatorTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_creator_text));
            if (event.isAvailable()) {
                holder.mTimelineBar.setBackgroundColor(event.getAvailableColor());
                holder.mEventTextViewsLayout.setBackground(null);
            }
            else {
                holder.mTimelineBar.setBackgroundColor(event.getBusyColor());
                BusyBgDrawable bg = new BusyBgDrawable(event.getBusyBgColor(), Color.WHITE);
                holder.mEventTextViewsLayout.setBackground(bg);
            }
            holder.mCurrentTimeLayout.setVisibility(View.GONE);

        }


        // Add Text views
        holder.mTimestampsLayout.removeAllViews();

        if (mTimestampTextViews.get(position) != null) {

            for (int i = 0; i < mTimestampTextViews.get(position).size(); i++) {
                TextView view = mTimestampTextViews.get(position).get(i);
                if (TimeHelper.getCurrentTimeInMillis() >= event.getTimeStamps().get(i) - TimeHelper.min2Millis(3) &&
                    TimeHelper.getCurrentTimeInMillis() <= event.getTimeStamps().get(i) + TimeHelper.min2Millis(3)) {
                    continue;
                }
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                holder.mTimestampsLayout.addView(view);
            }
        }


        // Add hidden text views
        if (mHiddenTvs.get(position) != null) {
            if (mHiddenTvs.get(position).getParent() != null) {
                ((ViewGroup) mHiddenTvs.get(position).getParent()).removeView(mHiddenTvs.get(position));
            }
            holder.mTimestampsLayout.addView(mHiddenTvs.get(position));
        }


        for (int i = 0 ; i < mDividers.size(); i++) {
            for (View view : mDividers.get(i)) {
                holder.mDividersLayout.removeView(view);
            }
        }

        // Add hourly divider
        if (mDividers.get(position) != null) {

            // Add corresponding view
            for (View view : mDividers.get(position)) {
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                holder.mDividersLayout.addView(view, 0);
            }
        }

        if (!event.isExpired()) {
            holder.mDividersLayout.setOnClickListener(v -> {
                if (this.mOnItemClickListener != null) {
                    if (event.isAvailable()) {
                        mOnItemClickListener.onAvailableEventItemClicked(position, mEventModelList);
                    } else {
                        mOnItemClickListener.onBusyEventItemClicked(event);
                    }
                }
            });
        }
        else {
            holder.mDividersLayout.setEnabled(false);
            holder.mDividersLayout.setOnClickListener(null);
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
        void onAvailableEventItemClicked(int position, ArrayList<EventModel> eventModelList);
        void onBusyEventItemClicked(EventModel eventModel);
    }

    private void createTimestampTextViewsAndHourlyDivider(List<EventModel> eventModelList) {

        for (int i = 0; i < eventModelList.size(); i++) {

            EventModel eventModel = eventModelList.get(i);

            List<TextView> textViewList = new ArrayList<>();
            List<View> dividerList = new ArrayList<>();
            List<FrameLayout> layoutList = new ArrayList<>();

            for (int j = 0; j < eventModel.getTimeStamps().size(); j++) {

                long ts = eventModel.getTimeStamps().get(j);

                TextView timestampTv = new TextView(mContext);
                timestampTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_timestamp_color));
                timestampTv.setText(TimeHelper.formatTime(ts));

                timestampTv.measure(0, 0);

                float textViewHeight = timestampTv.getMeasuredHeight();

                float textOffset = textViewHeight / 2f;

                float topMargin = (ts - eventModel.getStartTime()) * HEIGHT_PER_MILLIS - textOffset;

                timestampTv.setY(topMargin);

                textViewList.add(timestampTv);


                if (TimeHelper.getMin(ts) == 0) {
                    if (ts == eventModel.getStartTime() && eventModel.isBusy()) {

                    } else {
                        View view = new View(mContext);
                        view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_hourly_divider_color));
                        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.item_vertical_event_hourly_divider_height));
                        dividerParams.setMargins(0, (int) (topMargin + textOffset), 0, 0);
                        view.setLayoutParams(dividerParams);
                        dividerList.add(view);
                    }
                }
            }
            mDividers.put(i, dividerList);
            mTimestampTextViews.put(i, textViewList);
            mSubLayouts.put(i, layoutList);
        }
    }
}
