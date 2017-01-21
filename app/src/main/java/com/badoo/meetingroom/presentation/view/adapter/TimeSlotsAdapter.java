package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
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
    private final float MIN_SLOT_WIDTH;
    private final float MAX_SLOT_WIDTH;
    private final float WIDTH_PER_MILLIS;

    private List<EventModel> mEventModelList;
    private List<Boolean> mSelectedList;
    private int mStartSelectedPos = -1;
    private int mEndSelectedPos = -1;
    private OnItemClickListener mOnItemClickListener;

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
        mContext = context;
        MIN_SLOT_WIDTH = mContext.getResources().getDimension(R.dimen.room_booking_time_slot_width) / 2f;
        MAX_SLOT_WIDTH = mContext.getResources().getDimension(R.dimen.room_booking_time_slot_width);
        WIDTH_PER_MILLIS = MAX_SLOT_WIDTH / mDefaultSlotLength;
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

        viewWidth = viewWidth < MIN_SLOT_WIDTH ? MIN_SLOT_WIDTH : viewWidth;
        viewWidth = viewWidth > MAX_SLOT_WIDTH ? MAX_SLOT_WIDTH : viewWidth;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) viewWidth,
            (int) mContext.getResources().getDimension(R.dimen.room_booking_time_slot_height));

        holder.mTimeSlotImg.setLayoutParams(params);

        holder.mStartTimeTv.setText(eventModel.getStartTimeInText());

        Drawable timeSlotBg = drawTimeSlotShape(position);
        changeTimeSlotBg(eventModel, position, timeSlotBg);
        holder.mTimeSlotImg.setBackground(timeSlotBg);

        holder.itemView.setOnClickListener(v -> {

            if (eventModel.isAvailable()) {

                if (mStartSelectedPos == -1 || mEndSelectedPos == -1 || (mStartSelectedPos == position && mEndSelectedPos == position)) {

                    mSelectedList.set(position, !mSelectedList.get(position));
                    if (mSelectedList.get(position)) {
                        mStartSelectedPos = position;
                        mEndSelectedPos = position;
                    }
                    else {
                        mStartSelectedPos = -1;
                        mEndSelectedPos = -1;
                    }
                    notifyItemChanged(position);
                }
                else {
                    int i = mStartSelectedPos < position ? mStartSelectedPos : position;
                    int j = mStartSelectedPos < position ? position : mStartSelectedPos;
                    boolean hasBreakPoint = false;
                    for (int k = i; k <= j; k++) {
                        if (mEventModelList.get(k).isBusy()) {
                            hasBreakPoint = true;
                            break;
                        }
                    }
                    if (hasBreakPoint) {
                        int temp = mStartSelectedPos;

                        mSelectedList.set(position, !mSelectedList.get(position));
                        notifyItemChanged(position);

                        // Remove previous selected slots
                        while (mSelectedList.get(temp) == mSelectedList.get(position) &&
                            mEventModelList.get(temp).isAvailable() && temp < getItemCount() - 1) {
                            mSelectedList.set(temp, !mSelectedList.get(position));
                            notifyItemChanged(temp);
                            temp++;
                        }
                        mStartSelectedPos = position;
                        mEndSelectedPos = position;
                    }
                    else {

                        if (position < mStartSelectedPos) {

                            for (int p = position; p < mStartSelectedPos; p++) {
                                mSelectedList.set(p, mSelectedList.get(mStartSelectedPos));
                                notifyItemChanged(p);
                            }

                            mStartSelectedPos = position;

                        }
                        else if (position == mStartSelectedPos) {
                            mSelectedList.set(position, !mSelectedList.get(position));
                            notifyItemChanged(position);
                            mStartSelectedPos++;
                        }
                        else {

                            mSelectedList.set(position, !mSelectedList.get(position));
                            notifyItemChanged(position);

                            for (int p = mEndSelectedPos + 1; p < position; p++) {
                                mSelectedList.set(p, mSelectedList.get(mStartSelectedPos));
                                notifyItemChanged(p);
                            }

                            if (!mSelectedList.get(position)) {
                                for (int p = position + 1; p <= mEndSelectedPos; p++) {
                                    mSelectedList.set(p, !mSelectedList.get(mStartSelectedPos));
                                    notifyItemChanged(p);
                                }
                                mEndSelectedPos = position - 1;
                            }
                            else {
                                mEndSelectedPos = position;
                            }
                        }
                    }
                }
            }

            mOnItemClickListener.onEventItemClicked(mStartSelectedPos, mEndSelectedPos);

        });
    }


    private Drawable drawTimeSlotShape(int position) {

        Drawable drawable = null;

        if (getItemCount() == 1) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
            return drawable;
        }


        if (position == 0 && position < getItemCount() - 1) {
            int rightStatus = mEventModelList.get(position + 1).getStatus();
            if (rightStatus == mEventModelList.get(position).getStatus()) {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_left_slot);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
            }
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

    public void setOnItemClickListener (TimeSlotsAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEventItemClicked(int startIndex, int endIndex);
    }
}
