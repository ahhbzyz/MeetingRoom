package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.ViewHolder> {

    private Context mContext;
    private final long mDefaultSlotLength = TimeHelper.min2Millis(15);
    private final float WIDTH_PER_MILLIS;
    private int mRecyclerViewWidth = -1;
    private int mLeftPadding = -1;
    private List<EventModel> mEventModelList;
    private List<Boolean> mSelectedList;
    private int mFirstSelectedPos = -1;
    private int mLastSelectedPos = -1;

    public void setEventModelList(List<EventModel> eventModelList) {
        mEventModelList = eventModelList;
        mSelectedList = new ArrayList<>();
        for (EventModel eventModel : eventModelList) {
            if (eventModel.isAvailable()) {
                mSelectedList.add(false);
            }
            else {
                mSelectedList.add(true);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_start_time)
        TextView mStartTimeTv;
        @BindView(R.id.img_time_slot)
        ImageView mTimeSlotImg;


        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    TimeSlotsAdapter(Context context) {
        this.mContext = context;
        WIDTH_PER_MILLIS = mContext.getResources().getDimension(R.dimen.room_booking_time_slot_width) / mDefaultSlotLength;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        EventModel eventModel = mEventModelList.get(position);

        float viewWidth = eventModel.getDuration() * WIDTH_PER_MILLIS;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) viewWidth,
            (int) mContext.getResources().getDimension(R.dimen.room_booking_time_slot_height));
        //holder.mTimeSlotImg.setLayoutParams(params);

        holder.mStartTimeTv.setText(eventModel.getStartTimeInText());

        Drawable timeSlotBg = drawTimeSlotShape(position);
        changeTimeSlotBg(eventModel, position, timeSlotBg);
        holder.mTimeSlotImg.setBackground(timeSlotBg);

        holder.itemView.setOnClickListener(v -> {

            if (eventModel.isAvailable()) {

                if (mFirstSelectedPos == -1 || mLastSelectedPos == -1 || (mFirstSelectedPos == position && mLastSelectedPos == position)) {

                    mSelectedList.set(position, !mSelectedList.get(position));
                    if (mSelectedList.get(position)) {
                        mFirstSelectedPos = position;
                        mLastSelectedPos = position;
                    }
                    else {
                        mFirstSelectedPos = -1;
                        mLastSelectedPos = -1;
                    }
                    notifyItemChanged(position);
                }
                else {
                    int i = mFirstSelectedPos < position ? mFirstSelectedPos : position;
                    int j = mFirstSelectedPos < position ? position : mFirstSelectedPos;
                    boolean hasBreakPoint = false;
                    for (int k = i; k <= j; k++) {
                        if (mEventModelList.get(k).isBusy()) {
                            hasBreakPoint = true;
                            break;
                        }
                    }
                    if (hasBreakPoint) {
                        int temp = mFirstSelectedPos;

                        mSelectedList.set(position, !mSelectedList.get(position));
                        notifyItemChanged(position);

                        // Remove previous selected slots
                        while (mSelectedList.get(temp) == mSelectedList.get(position) &&
                            mEventModelList.get(temp).isAvailable() && temp < getItemCount() - 1) {
                            mSelectedList.set(temp, !mSelectedList.get(position));
                            notifyItemChanged(temp);
                            temp++;
                        }
                        mFirstSelectedPos = position;
                        mLastSelectedPos = position;
                    }
                    else {

                        if (position < mFirstSelectedPos) {

                            for (int p = position; p < mFirstSelectedPos; p++) {
                                mSelectedList.set(p, mSelectedList.get(mFirstSelectedPos));
                                notifyItemChanged(p);
                            }

                            mFirstSelectedPos = position;

                        }
                        else if (position == mFirstSelectedPos) {
                            mSelectedList.set(position, !mSelectedList.get(position));
                            notifyItemChanged(position);
                            mFirstSelectedPos++;
                        }
                        else {

                            mSelectedList.set(position, !mSelectedList.get(position));
                            notifyItemChanged(position);

                            for (int p = mLastSelectedPos + 1; p < position; p++) {
                                mSelectedList.set(p, mSelectedList.get(mFirstSelectedPos));
                                notifyItemChanged(p);
                            }

                            if (!mSelectedList.get(position)) {
                                for (int p = position + 1; p <= mLastSelectedPos; p++) {
                                    mSelectedList.set(p, !mSelectedList.get(mFirstSelectedPos));
                                    notifyItemChanged(p);
                                }
                                mLastSelectedPos = position - 1;
                            }
                            else {
                                mLastSelectedPos = position;
                            }
                        }
                    }
                }
            }
        });
    }


    private Drawable drawTimeSlotShape(int position) {

        Drawable drawable = null;

        if (getItemCount() == 1) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
        }

        if (position == 0) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_left_slot);
            return drawable;
        }

        if (position == getItemCount() - 1 && position >= 1) {
            int leftStatus = mEventModelList.get(position - 1).getStatus();
            if (leftStatus == mEventModelList.get(position).getStatus()) {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_right_slot);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
            }
            return drawable;
        }

        int leftStatus = mEventModelList.get(position - 1).getStatus();
        int status = mEventModelList.get(position).getStatus();
        int rightStatus = mEventModelList.get(position + 1).getStatus();

        if (leftStatus == status && status == rightStatus) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_slot);
        }

        if (leftStatus != rightStatus) {

            if (status == EventModel.AVAILABLE) {

                if (leftStatus == EventModel.BUSY) {
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_left_slot);
                }
                else {
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_right_slot);
                }

            }
            else {
                if (leftStatus == EventModel.BUSY) {
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_right_slot);
                }
                else {
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_left_slot);
                }
            }
        }

        if (leftStatus == rightStatus && status != leftStatus) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
        }

        return drawable;
    }

    private void changeTimeSlotBg(EventModel eventModel, int position, Drawable drawable) {
        if (!mSelectedList.isEmpty() && mSelectedList.get(position)) {
            drawable.setColorFilter(eventModel.getEventColor(), PorterDuff.Mode.SRC_IN);
        }
        else {
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.timeSlotExpired), PorterDuff.Mode.SRC_IN);
        }
    }


    @Override
    public int getItemCount() {
        return mEventModelList == null ? 0 : mEventModelList.size();
    }

//    public void setOnItemClickListener (TimeSlotsAdapter.OnItemClickListener onItemClickListener) {
//        this.mOnItemClickListener = onItemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void onEventItemClicked(List<TimeSlot> mTimeSlotList);
//    }
}
