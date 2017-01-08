package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.ViewHolder>{

    private Context mContext;
    private List<TimeSlot> mTimeSlotList;
    private final long mDefaultSlotLength = 15 * 60 * 1000;
    private int mRecyclerViewWidth = -1;
    private int mLeftPadding = -1;
    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_start_time) TextView mStartTimeTv;
        @BindView(R.id.img_time_slot) ImageView mTimeSlotImg;
        @BindView(R.id.divider) View mDivider;
        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    TimeSlotsAdapter(Context context) {
        this.mContext = context;
        mTimeSlotList = new ArrayList<>();
    }

    public void setTimeSlots(long startTime, long endTime) {

        if (endTime < startTime || (endTime - startTime) < mDefaultSlotLength) {
            throw new IllegalArgumentException("End time cannot less than start time " +
                "or time period cannot less than 15 minutes");
        }
        int remainderOfStartTimeMin = getRemainderOfMin(startTime);
        int startOffset = remainderOfStartTimeMin > 5 ? 10 : 5;
        if (remainderOfStartTimeMin == 0) {
            startOffset = 0;
        }
        long newStartTime = startTime + (startOffset - remainderOfStartTimeMin) * 60 * 1000;
        int numOfSlots = (int) ((endTime - newStartTime) / mDefaultSlotLength);

        mTimeSlotList.add(new TimeSlot(startTime));
        if (numOfSlots <= 0) {
            mTimeSlotList.add(new TimeSlot(endTime));
        } else {
            for (int i = 0; i < numOfSlots; i++) {
                // TODO
                newStartTime = newStartTime + mDefaultSlotLength;
                mTimeSlotList.add(new TimeSlot(newStartTime));
            }
            if (!TimeHelper.isSameTimeIgnoreSec(newStartTime, endTime)) {
                mTimeSlotList.add(new TimeSlot(endTime));
            }
        }


        notifyDataSetChanged();
    }

    public void setRecyclerViewParams(int width, int leftPadding) {
        this.mRecyclerViewWidth = width;
        this.mLeftPadding = leftPadding;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TimeSlot slot = mTimeSlotList.get(position);
        holder.mStartTimeTv.setText(slot.getStartTimeInText());

        if (slot.isSelected) {

            if (getItemCount() == 2) {
                if (position == 0) {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_selected_slot));
                } else {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_busy_slot));
                }
            } else {
                if (position == 0) {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_selected_start_slot));
                } else if (position == getItemCount() - 2){
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_selected_end_slot));
                } else if (position == getItemCount() - 1) {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_busy_slot));
                } else {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_flat_selected_slot));
                }
            }

        } else {

            if (getItemCount() == 2) {
                if (position == 0) {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_unselected_slot));
                } else {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_busy_slot));
                }
            } else {
                if (position == 0) {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_unselected_start_slot));
                } else if (position == getItemCount() - 2){
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_unselected_end_slot));
                } else if (position == getItemCount() - 1) {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_busy_slot));
                } else {
                    holder.mTimeSlotImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_flat_unselected_slot));
                }
            }
        }

        if (position == getItemCount() - 1) {
            if (mRecyclerViewWidth != - 1 && mLeftPadding != -1 && mLeftPadding > mContext.getResources().getDimension(R.dimen.room_booking_view_margin)) {
                int slotWidth = (int) (mRecyclerViewWidth
                    - mLeftPadding
                    - mContext.getResources().getDimension(R.dimen.room_booking_time_slot_width) * (getItemCount() - 1)
                    - mContext.getResources().getDimension(R.dimen.room_booking_time_slot_divider_width) * (getItemCount() - 1));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    slotWidth,
                    (int) mContext.getResources().getDimension(R.dimen.room_booking_time_slot_height));
                holder.mTimeSlotImg.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) mContext.getResources().getDimension(R.dimen.room_booking_time_slot_width),
                    (int) mContext.getResources().getDimension(R.dimen.room_booking_time_slot_height));
                holder.mTimeSlotImg.setLayoutParams(params);
            }
            holder.mDivider.setVisibility(View.GONE);
            if (TimeHelper.isMidNight(slot.getStartTime())) {
                holder.mStartTimeTv.setText("24:00");
            }
        } else {
            holder.mDivider.setVisibility(View.INVISIBLE);

            holder.itemView.setOnClickListener(v -> {

                slot.setSelected(!slot.isSelected());

                boolean meetFirstSelectedSlot = false;
                for (int i = 0; i < position; i++) {
                    if (!mTimeSlotList.get(i).isSelected() && !meetFirstSelectedSlot) {
                        continue;
                    }
                    meetFirstSelectedSlot = true;
                    mTimeSlotList.get(i).setSelected(true);
                }

                int firstSelectedSlotAfterPos = position;
                for (int i = position + 1; i < mTimeSlotList.size(); i++) {
                    if (mTimeSlotList.get(i).isSelected()) {
                        firstSelectedSlotAfterPos = i;
                        break;
                    }
                }

                if (!slot.isSelected() && meetFirstSelectedSlot) {
                    for (int i = position + 1; i < mTimeSlotList.size(); i++) {
                        mTimeSlotList.get(i).setSelected(false);
                    }
                } else {
                    for (int i = position + 1; i <= firstSelectedSlotAfterPos; i++) {
                        mTimeSlotList.get(i).setSelected(true);
                    }
                }



                notifyDataSetChanged();
                if (this.mOnItemClickListener != null) {
                    this.mOnItemClickListener.onEventItemClicked(mTimeSlotList);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTimeSlotList.size();
    }

    private int getRemainderOfMin(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int min = calendar.get(Calendar.MINUTE);
        return min % 10;
    }


    public class TimeSlot {

        private long startTime;
        private boolean isSelected = false;

        TimeSlot(long startTime) {
            this.startTime = startTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public boolean isSelected() {
            return isSelected;
        }

        void setSelected(){
            isSelected = !isSelected;
        }

        void setSelected(boolean selected) {
            this.isSelected = selected;
        }

        String getStartTimeInText() {
            return TimeHelper.formatTime(startTime);
        }
    }

    public void setOnItemClickListener (TimeSlotsAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEventItemClicked(List<TimeSlot> mTimeSlotList);
    }
}
