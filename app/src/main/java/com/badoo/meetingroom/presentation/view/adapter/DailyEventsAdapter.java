package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
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
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.view.component.drawable.BusyBgDrawable;
import com.badoo.meetingroom.presentation.view.component.drawable.TimelineBarDrawable;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Gravity.CENTER;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.ViewHolder> {

    private static final int MIN_SLOT_TIME = 5;
    private static final float HEIGHT_PER_MILLIS = 64f / TimeHelper.min2Millis(MIN_SLOT_TIME);

    private final Context mContext;
    private ArrayList<EventModel> mEventModelList;
    private OnEventClickListener mOnEventClickListener;

    private List<ItemView> mItemViewList;
    private OnEventRenderFinishListener mOnEventRenderFinishListener;
    private BusyBgDrawable mComingBusyEventBg;

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
        @BindView(R.id.layout_text_views)
        RelativeLayout mTextViewsLayout;
        @BindView(R.id.layout_current_time)
        LinearLayout mCurrentTimeLayout;
        @BindView(R.id.tv_current_time)
        TextView mCurrentTimeTv;
        @BindView(R.id.view_top_divider)
        View mExpiredEventTopDivider;
        @BindView(R.id.view_bottom_divider)
        View mExpiredEventBottomDivider;
        @BindView(R.id.layout_dividers)
        RelativeLayout mDividersLayout;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    DailyEventsAdapter(Context context) {
        mContext = context;
        mEventModelList = new ArrayList<>();
        mItemViewList = new ArrayList<>();
        mComingBusyEventBg = new BusyBgDrawable(ContextCompat.getColor(mContext, R.color.roomStatusBusyBg), Color.WHITE);
    }

    public void setDailyEventList(List<EventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        mEventModelList = (ArrayList<EventModel>) roomEventModelList;

        Handler mCalcItemViewHandler = new Handler();

        final Runnable runnable = () -> {

            mItemViewList = calculateItemView(mEventModelList);
            notifyDataSetChanged();
            mOnEventRenderFinishListener.onEventRenderFinish();
        };

        mCalcItemViewHandler.post(runnable);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_vertical_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mItemViewList.isEmpty()) {
            return;
        }

        // Current event
        EventModel eventModel = mEventModelList.get(position);
        ItemView itemView = mItemViewList.get(position);

        // Calculate item height
        float viewHeight = itemView.getViewHeight();
        // Remaining progress of event
        float remainingProgress = eventModel.getRemainingTime() / (float) eventModel.getDuration();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) viewHeight);
        holder.itemView.setLayoutParams(params);

        // Reset view child
        holder.mEventPeriodTv.setVisibility(View.VISIBLE);
        holder.mEventCreatorTv.setVisibility(View.VISIBLE);
        holder.mEventTitle.setVisibility(View.VISIBLE);
        holder.mExpiredEventTopDivider.setVisibility(View.GONE);
        holder.mExpiredEventBottomDivider.setVisibility(View.GONE);
        holder.mCurrentTimeLayout.setVisibility(View.GONE);
        holder.mEventTextViewsLayout.setOrientation(LinearLayout.VERTICAL);

        holder.mEventPeriodTv.measure(0, 0);

        // Change orientation when view height too small
        if (viewHeight < holder.mEventPeriodTv.getMeasuredHeight() * 3 + mContext.getResources().getDimension(R.dimen.item_vertical_event_text_views_top_padding)) {
            holder.mEventTextViewsLayout.setOrientation(LinearLayout.HORIZONTAL);
            holder.mEventTitle.setVisibility(View.GONE);
        }

        // Set Text
        if (eventModel.isFastBooking()) {
            holder.mEventTitle.setText(mContext.getString(R.string.fast_booking));
            holder.mEventCreatorTv.setText(mContext.getString(R.string.no_name_available));
        }
        else {

            if (eventModel.getEventTitle() != null) {
                holder.mEventTitle.setText(eventModel.getEventTitle());
            }
            else {
                holder.mEventTitle.setVisibility(View.GONE);
            }

            String creatorName = eventModel.getCreatorName();
            String creatorEmail = eventModel.getCreatorEmailAddress();

            if (creatorName != null && creatorEmail != null) {
                holder.mEventCreatorTv.setText(creatorName + " (" + creatorEmail + ")");
            }
            else if (creatorName != null) {
                holder.mEventCreatorTv.setText(creatorName);
            }
            else {
                holder.mEventCreatorTv.setText(creatorEmail);
            }
        }

        if (eventModel.isAvailable()) {
            holder.mEventPeriodTv.setText("");
        }
        else {
            holder.mEventPeriodTv.setText(eventModel.getDurationInText());
        }

        holder.mEventTextViewsLayout.setBackground(null);


        // Event Expired
        if (eventModel.isExpired()) {

            if (eventModel.isBusy()) {
                holder.mExpiredEventTopDivider.setVisibility(View.VISIBLE);
                holder.mExpiredEventBottomDivider.setVisibility(View.VISIBLE);
                holder.mTimelineBar.setBackgroundColor(eventModel.getExpiredEventColor(mContext));
            }
            else {
                holder.mTimelineBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_expired_time_line_bar));
            }

            holder.mEventTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_title_text_grey));
            holder.mEventCreatorTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_creator_text_grey));

            holder.mEventTextViewsLayout.setBackground(null);
        }

        // Event is processing
        if (eventModel.isProcessing()) {

            holder.mCurrentTimeLayout.setVisibility(View.VISIBLE);
            holder.mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());


            holder.mEventTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_title_text));
            holder.mEventCreatorTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_creator_text));


            if (eventModel.isAvailable()) {
                TimelineBarDrawable barDrawable = new TimelineBarDrawable(ContextCompat.getColor(mContext, R.color.item_vertical_event_expired_time_line_bar), eventModel.getAvailableEventColor(mContext));
                barDrawable.setRemainingProgress(remainingProgress);
                holder.mTimelineBar.setBackground(barDrawable);
                holder.mEventTextViewsLayout.setBackground(null);
            }
            else {
                TimelineBarDrawable barDrawable = new TimelineBarDrawable(eventModel.getExpiredEventColor(mContext), eventModel.getBusyEventColor(mContext));
                barDrawable.setRemainingProgress(remainingProgress);
                holder.mTimelineBar.setBackground(barDrawable);

                BusyBgDrawable bg = new BusyBgDrawable(eventModel.getBusyEventBgColor(mContext), Color.WHITE, remainingProgress);
                holder.mEventTextViewsLayout.setBackground(bg);
                holder.mEventPeriodTv.setText(eventModel.getDurationInText());
            }

            float topTimelineBarHeight = eventModel.getDuration() * HEIGHT_PER_MILLIS * (1 - remainingProgress);
            holder.mCurrentTimeLayout.setY(topTimelineBarHeight);
            holder.mCurrentTimeLayout.measure(0, 0);
            holder.mCurrentTimeTv.setY(-holder.mCurrentTimeLayout.getMeasuredHeight() / 2f);

        }

        // Event is coming
        if (eventModel.isComing()) {
            holder.mEventTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_title_text));
            holder.mEventCreatorTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_creator_text));
            if (eventModel.isAvailable()) {
                holder.mTimelineBar.setBackgroundColor(eventModel.getAvailableEventColor(mContext));
                holder.mEventTextViewsLayout.setBackground(null);
            }
            else {
                holder.mTimelineBar.setBackgroundColor(eventModel.getBusyEventColor(mContext));
                holder.mEventTextViewsLayout.setBackground(mComingBusyEventBg);
            }
        }


        // Add Text views
        holder.mTimestampsLayout.removeAllViews();

        List<TextView> textViewList = itemView.getTextViewList();
        if (textViewList != null) {

            for (int i = 0; i < textViewList.size(); i++) {

                TextView view = textViewList.get(i);

                if (TimeHelper.getCurrentTimeInMillis() >= eventModel.getTimeStamps().get(i) - TimeHelper.min2Millis(2) &&
                    TimeHelper.getCurrentTimeInMillis() <= eventModel.getTimeStamps().get(i) + TimeHelper.min2Millis(2)) {
                    continue;
                }

                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                holder.mTimestampsLayout.addView(view);
            }
        }


        // Add hidden text views
        TextView hiddenTv = itemView.getHiddenTextView();
        if (hiddenTv != null) {
            if (hiddenTv.getParent() != null) {
                ((ViewGroup) hiddenTv.getParent()).removeView(hiddenTv);
            }
            holder.mTimestampsLayout.addView(hiddenTv);
        }

        // Add hourly divider
        List<View> dividerList = itemView.getDividerList();

        holder.mDividersLayout.removeAllViews();

        if (dividerList != null) {
            for (View view : dividerList) {
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                holder.mDividersLayout.addView(view);
            }
        }


        // Click listener
        if (!eventModel.isExpired()) {
            holder.mTextViewsLayout.setOnClickListener(v -> {
                if (this.mOnEventClickListener != null) {
                    if (eventModel.isAvailable()) {
                        mOnEventClickListener.onAvailableEventClicked(position, mEventModelList);
                    }
                    else {
                        mOnEventClickListener.onBusyEventClicked(eventModel);
                    }
                }
            });
        }
        else {
            holder.mTextViewsLayout.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mEventModelList.size();
    }

    private List<ItemView> calculateItemView(List<EventModel> eventModelList) {

        List<ItemView> itemViewList = new ArrayList<>();

        for (int i = 0; i < eventModelList.size(); i++) {

            EventModel eventModel = eventModelList.get(i);

            ItemView itemView = new ItemView();

            float viewHeight = eventModel.getDuration() * HEIGHT_PER_MILLIS;
            itemView.setViewHeight(viewHeight);

            itemView.setTextViewList(createTimestampTextViews(i, eventModel));
            itemView.setDividerList(createHourlyDividers(eventModel));

            TextView hiddenTv = new TextView(mContext);
            hiddenTv.setText(mContext.getString(R.string.fake_time));
            hiddenTv.setVisibility(View.INVISIBLE);
            itemView.setHiddenTextView(hiddenTv);

            itemViewList.add(itemView);
        }

        return itemViewList;
    }

    private List<TextView> createTimestampTextViews(int position, EventModel eventModel) {

        List<TextView> textViewList = new ArrayList<>();

        for (int i = 0; i < eventModel.getTimeStamps().size(); i++) {

            long ts = eventModel.getTimeStamps().get(i);

            TextView timestampTv = new TextView(mContext);

            timestampTv.setTextColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_timestamp_color));
            timestampTv.setText(TimeHelper.formatTime(ts));

            timestampTv.measure(0, 0);
            timestampTv.setHeight(timestampTv.getMeasuredHeight());


            float textViewHeight = timestampTv.getMeasuredHeight();

            float textOffset = textViewHeight / 2f;

            float topMargin = (ts - eventModel.getStartTime()) * HEIGHT_PER_MILLIS - textOffset;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(CENTER);

            if (position == getItemCount() - 1 && i == eventModel.getTimeStamps().size() - 1) {
                timestampTv.setLayoutParams(params);
                timestampTv.setY(topMargin);
            }
            else {
                params.setMargins(0, (int) topMargin, 0, 0);
                timestampTv.setLayoutParams(params);
            }

            textViewList.add(timestampTv);


        }
        return textViewList;
    }

    private List<View> createHourlyDividers(EventModel eventModel) {

        List<View> dividerList = new ArrayList<>();

        for (int i = 0; i < eventModel.getTimeStamps().size(); i++) {

            long ts = eventModel.getTimeStamps().get(i);

            if (TimeHelper.getMin(ts) == 0) {
                if (ts == eventModel.getStartTime() && eventModel.isBusy()) {

                }
                else {
                    View view = new View(mContext);
                    view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_vertical_event_hourly_divider_color));
                    RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.item_vertical_event_hourly_divider_height));
                    float topMargin = (ts - eventModel.getStartTime()) * HEIGHT_PER_MILLIS;
                    dividerParams.setMargins(0, (int) (topMargin), 0, 0);
                    view.setLayoutParams(dividerParams);
                    dividerList.add(view);
                }
            }
        }

        return dividerList;
    }


    private class ItemView {

        private float viewHeight;
        private List<TextView> textViewList;
        private TextView hiddenTextView;
        private List<View> dividerList;

        float getViewHeight() {
            return viewHeight;
        }

        void setViewHeight(float viewHeight) {
            this.viewHeight = viewHeight;
        }

        List<TextView> getTextViewList() {
            return textViewList;
        }

        void setTextViewList(List<TextView> textViewList) {
            this.textViewList = textViewList;
        }

        TextView getHiddenTextView() {
            return hiddenTextView;
        }

        void setHiddenTextView(TextView hiddenTextView) {
            this.hiddenTextView = hiddenTextView;
        }

        List<View> getDividerList() {
            return dividerList;
        }

        void setDividerList(List<View> dividerList) {
            this.dividerList = dividerList;
        }

    }


    public void setOnEventRenderFinishListener(OnEventRenderFinishListener onEventRenderFinishListener) {
        this.mOnEventRenderFinishListener = onEventRenderFinishListener;
    }

    public interface OnEventRenderFinishListener {
        void onEventRenderFinish();
    }

    public void setOnItemClickListener(OnEventClickListener onEventClickListener) {
        this.mOnEventClickListener = onEventClickListener;
    }

    public interface OnEventClickListener {
        void onAvailableEventClicked(int position, ArrayList<EventModel> eventModelList);

        void onBusyEventClicked(EventModel eventModel);
    }

}
