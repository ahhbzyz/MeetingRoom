package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.os.Handler;
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
import com.badoo.meetingroom.presentation.view.component.drawable.TimelineBarDrawable;
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


    private float MAX_BUSY_SLOT_WIDTH = 375;
    private float MIN_BUSY_SLOT_WIDTH = 125;
    private float SLOT_WIDTH_WITH_DASHES;

    private float MIN_AVAILABLE_SLOT_WIDTH = 125;
    private float AVAILABLE_SLOT_WIDTH = 250;


    private float BUSY_SLOT_WIDTH_PER_MILLIS = MIN_BUSY_SLOT_WIDTH / (float) TimeHelper.min2Millis(15);
    private float AVAILABLE_SLOT_WIDTH_PER_MILLIS = AVAILABLE_SLOT_WIDTH / (float) TimeHelper.min2Millis(15);


    private List<EventModel> mEventModelList;
    private List<ItemView> mItemViewList;

    private OnEventClickListener mOnEventClickListener;

    private Context mContext;
    private OnEventRenderFinishListener mOnEventRenderFinishListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_start_time) TextView mStartTimeTv;
        @BindView(R.id.img_timeline_bar) ImageView mTimelineBarImg;
        @BindView(R.id.layout_current_time) LinearLayout mCurrentTimeLayout;
        @BindView(R.id.tv_current_time) TextView mCurrentTimeTv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    HorizontalTimelineAdapter(Context context) {
        mEventModelList = new ArrayList<>();
        mContext = context;
        SLOT_WIDTH_WITH_DASHES = MAX_BUSY_SLOT_WIDTH + TimelineBarDrawable.numOfDashes * mContext.getResources().getDimension(R.dimen.item_horizontal_event_divider_width);
    }

    public void setEventList(List<EventModel> roomEventModelList) {
        if (roomEventModelList == null) {
            throw new IllegalArgumentException("Room event list cannot be null");
        }
        mEventModelList = roomEventModelList;

        Handler mCalcItemViewHandler = new Handler();

        final Runnable runnable = () -> {
            mItemViewList = calcItemView(mEventModelList);
            mOnEventRenderFinishListener.onEventRenderFinish();
            notifyDataSetChanged();
        };

        mCalcItemViewHandler.post(runnable);

    }

    private List<ItemView> calcItemView(List<EventModel> mEvents) {

        List<ItemView> itemViewList = new ArrayList<>();

        for (EventModel eventModel : mEvents) {

            ItemView itemView = new ItemView();

            float viewWidth;

            if (eventModel.isAvailable()) {

                viewWidth = eventModel.getDuration() * AVAILABLE_SLOT_WIDTH_PER_MILLIS;

                if (viewWidth < MIN_AVAILABLE_SLOT_WIDTH) {

                    viewWidth = MIN_AVAILABLE_SLOT_WIDTH;

                }

            } else {

                viewWidth = eventModel.getDuration() * BUSY_SLOT_WIDTH_PER_MILLIS;

                if (viewWidth < MIN_BUSY_SLOT_WIDTH) {

                    viewWidth = MIN_BUSY_SLOT_WIDTH;

                } else if (viewWidth > MAX_BUSY_SLOT_WIDTH) {

                    viewWidth = SLOT_WIDTH_WITH_DASHES;

                }

            }

            itemView.setViewWidth(viewWidth);

            itemViewList.add(itemView);
        }

        return itemViewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_horizontal_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mItemViewList == null || mItemViewList.isEmpty()) {
            return;
        }

        EventModel eventModel = mEventModelList.get(position);
        ItemView itemView = mItemViewList.get(position);

        float viewWidth = itemView.getViewWidth();
        float remainingProgress = eventModel.getRemainingTime() / (float) eventModel.getDuration();


        RelativeLayout.LayoutParams params = new
            RelativeLayout.LayoutParams((int) viewWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        holder.itemView.setLayoutParams(params);

        holder.mStartTimeTv.setText(eventModel.getStartTimeInText());
        holder.mStartTimeTv.measure(0, 0);

        // 5 offset
        if (viewWidth >= holder.mStartTimeTv.getMeasuredWidth() + 5) {
            holder.mStartTimeTv.setVisibility(View.VISIBLE);
        } else {
            holder.mStartTimeTv.setVisibility(View.INVISIBLE);
        }

        TimelineBarDrawable barDrawable
            = new TimelineBarDrawable(eventModel.getEventExpiredColor(), eventModel.getEventColor());
        barDrawable.setRemainingProgress(remainingProgress);
        barDrawable.setCornerRadius(0, 0, 0, 0);

        if (position == 0) {
            barDrawable.setCornerRadius(1, 0, 0, 0);
        }

        if (position == getItemCount() - 1) {
            barDrawable.setCornerRadius(0, 0, 0, 1);
        }

//        if (!eventModel.isComing()) {
//
//
//        } else {
//            if (position == 0) {
//                barDrawable.setCornerRadius(0, 0, 1, 0);
//            }
//
//            if (position == getItemCount() - 1) {
//                barDrawable.setCornerRadius(0, 0, 0, 1);
//            }
//        }

        if (eventModel.isBusy() && viewWidth > MAX_BUSY_SLOT_WIDTH) {
            barDrawable.setHasDashes(true, mContext.getResources().getDimension(R.dimen.item_horizontal_event_divider_width));
        }


        barDrawable.setOrientation(TimelineBarDrawable.HORIZONTAL);

        holder.mTimelineBarImg.setBackground(barDrawable);

        if (!eventModel.isExpired()) {
            if (eventModel.isAvailable()) {
                holder.itemView.setOnClickListener(v -> mOnEventClickListener.onAvailableEventClick(position, mEventModelList));
            } else {
                holder.itemView.setOnClickListener(v -> mOnEventClickListener.onBusyEventClicked(eventModel));
            }
        } else {
            holder.itemView.setOnClickListener(null);
        }



        if (eventModel.isProcessing()) {
            float leftTimelineBarWidth = viewWidth * (1 - remainingProgress);
            holder.mCurrentTimeLayout.setVisibility(View.VISIBLE);
            holder.mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
            holder.mCurrentTimeLayout.setX(leftTimelineBarWidth);
            holder.mCurrentTimeLayout.measure(0, 0);
            holder.mCurrentTimeTv.setX(-holder.mCurrentTimeLayout.getMeasuredWidth() / 2f);
        } else {
            holder.mCurrentTimeLayout.setVisibility(View.GONE);
        }
    }


    public float getPastTimeWidth() {
        if (mEventModelList == null || mItemViewList == null || mEventModelList.isEmpty() || mItemViewList.isEmpty()) {
            return -1;
        }
        float width = 0;
        int i = 0;
        for (; i < mEventModelList.size(); i++) {
            EventModel eventModel = mEventModelList.get(i);
            if (eventModel.isProcessing()) {
                float leftTimelineBarWidth = mItemViewList.get(i).getViewWidth() * (1 - eventModel.getRemainingTime() / (float) eventModel.getDuration());
                width += leftTimelineBarWidth;
                break;
            }
            width += mItemViewList.get(i).getViewWidth();
        }

        width += i * mContext.getResources().getDimension(R.dimen.item_vertical_event_white_divider_height);
        return width;
    }

    @Override
    public int getItemCount() {
        return mEventModelList.size();
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        mOnEventClickListener = onEventClickListener;
    }

    public interface OnEventClickListener {
        void onAvailableEventClick(int position, List<EventModel> eventModelList);
        void onBusyEventClicked(EventModel eventModel);
    }

    private class ItemView {
        private float viewWidth;

        float getViewWidth() {
            return viewWidth;
        }

        void setViewWidth(float viewWidth) {
            this.viewWidth = viewWidth;
        }

    }

    public void setOnEventRenderFinishListener(OnEventRenderFinishListener onEventRenderFinishListener) {
        this.mOnEventRenderFinishListener = onEventRenderFinishListener;
    }

    public interface OnEventRenderFinishListener {
        void onEventRenderFinish();
    }
}
