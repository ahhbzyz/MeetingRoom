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
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.view.component.drawable.TimeSlotWithDashesDrawable;
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
    private final float DEFAULT_SLOT_WIDTH;
    private final float MIN_SLOT_WIDTH;
    private final float MAX_SLOT_WIDTH;
    private final float WIDTH_PER_MILLIS;

    private int mStartSelectedPos = -1;
    private int mEndSelectedPos = -1;

    private List<EventModel> mEventModelList;
    private List<ItemView> mItemViewList;


    private OnItemClickListener mOnItemClickListener;



    public void setEventModelList(List<EventModel> eventModelList) {
        mEventModelList = eventModelList;
        mItemViewList = calcItemView(mEventModelList);
    }

    private List<ItemView> calcItemView(List<EventModel> mEventModelList) {

        List<ItemView> itemViewList = new ArrayList<>();

        long accBookedTime = 0;

        for (int i = 0; i < mEventModelList.size(); i ++) {

            EventModel eventModel = mEventModelList.get(i);

            ItemView itemView = new ItemView();
            itemViewList.add(itemView);

            float viewWidth = eventModel.getDuration() * WIDTH_PER_MILLIS;

            if (eventModel.isAvailable()) {

                accBookedTime = 0;
                itemView.setSelected(false);
                viewWidth = viewWidth < MIN_SLOT_WIDTH ? MIN_SLOT_WIDTH : viewWidth;
                viewWidth = viewWidth > MAX_SLOT_WIDTH ? MAX_SLOT_WIDTH : viewWidth;

            } else {

                accBookedTime += eventModel.getDuration();
                itemView.setSelected(true);

                int j = i;

                if (accBookedTime > TimeHelper.min2Millis(30)) {

                    while (j >= 0 && mEventModelList.get(j).isBusy()) {

                        itemViewList.get(j).setViewWidth(0);
                        j--;
                    }
                    viewWidth = 0;
                    itemViewList.get(j+1).setViewWidth(MAX_SLOT_WIDTH + 1);

                } else {
                    viewWidth = viewWidth < MIN_SLOT_WIDTH ? MIN_SLOT_WIDTH : viewWidth;
                    viewWidth = viewWidth > MAX_SLOT_WIDTH ? MAX_SLOT_WIDTH : viewWidth;
                }
            }

            itemView.setViewWidth(viewWidth);
        }

        return itemViewList;
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
        DEFAULT_SLOT_WIDTH = mContext.getResources().getDimension(R.dimen.room_booking_time_slot_width);
        MIN_SLOT_WIDTH = DEFAULT_SLOT_WIDTH / 2f;
        MAX_SLOT_WIDTH = DEFAULT_SLOT_WIDTH * 2f;
        WIDTH_PER_MILLIS = DEFAULT_SLOT_WIDTH / mDefaultSlotLength;
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

        ItemView itemView = mItemViewList.get(position);

        float viewWidth = itemView.getViewWidth();

        if (viewWidth == 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) viewWidth, 0);
            holder.itemView.setLayoutParams(params);
            return;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.itemView.setLayoutParams(params);


        if (viewWidth <= MAX_SLOT_WIDTH) {
            Drawable timeSlotBg = drawTimeSlotShape(position);
            changeTimeSlotBgColor(eventModel, position, timeSlotBg);
            holder.mTimeSlotImg.setBackground(timeSlotBg);
        } else {
            TimeSlotWithDashesDrawable drawable = new TimeSlotWithDashesDrawable(mContext, eventModel.getEventColor(), mContext.getResources().getDimension(R.dimen.room_booking_time_slot_divider_width));
            holder.mTimeSlotImg.setBackground(drawable);
        }

        holder.mStartTimeTv.setText(eventModel.getStartTimeInText());

        holder.itemView.setOnClickListener(v -> {

            if (!eventModel.isAvailable()) {
                return;
            }

            // Only one item clicked
            if (mStartSelectedPos == -1 || mEndSelectedPos == -1 || (mStartSelectedPos == position && mEndSelectedPos == position)) {

                itemView.setSelected(!itemView.isSelected());

                if (itemView.isSelected()) {
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
                // If has a break point between start pos end click pos
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
                    itemView.setSelected(!itemView.isSelected());
                    notifyItemChanged(position);

                    // Remove previous selected slots
                    while (mItemViewList.get(temp).isSelected() == itemView.isSelected() &&
                        mEventModelList.get(temp).isAvailable() && temp < getItemCount() - 1) {
                        mItemViewList.get(temp).setSelected(!itemView.isSelected());
                        notifyItemChanged(temp);
                        temp++;
                    }
                    mStartSelectedPos = position;
                    mEndSelectedPos = position;
                }

                // draw continues slot
                else {

                    if (position < mStartSelectedPos) {

                        for (int p = position; p < mStartSelectedPos; p++) {
                            mItemViewList.get(p).setSelected(mItemViewList.get(mStartSelectedPos).isSelected());
                            notifyItemChanged(p);
                        }

                        mStartSelectedPos = position;

                    }
                    else if (position == mStartSelectedPos) {
                        itemView.setSelected(!itemView.isSelected());
                        notifyItemChanged(position);
                        mStartSelectedPos++;
                    }
                    else {
                        itemView.setSelected(!itemView.isSelected());
                        notifyItemChanged(position);

                        for (int p = mEndSelectedPos + 1; p < position; p++) {
                            mItemViewList.get(p).setSelected(mItemViewList.get(mStartSelectedPos).isSelected());
                            notifyItemChanged(p);
                        }

                        if (!itemView.isSelected()) {
                            for (int p = position + 1; p <= mEndSelectedPos; p++) {
                                mItemViewList.get(p).setSelected(!mItemViewList.get(mStartSelectedPos).isSelected());
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


            mOnItemClickListener.onEventItemClicked(mStartSelectedPos, mEndSelectedPos);

        });
    }


    private Drawable drawTimeSlotShape(int position) {

        Drawable drawable = null;


        // Only one item
        if (getItemCount() == 1) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
            return drawable;
        }


        // first item
        if (position == 0) {
            int rightStatus = mEventModelList.get(position + 1).getStatus();
            if (rightStatus == mEventModelList.get(position).getStatus()) {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_left_slot);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
            }
            return drawable;
        }

        // last item
        if (position == getItemCount() - 1) {
            int leftStatus = mEventModelList.get(position - 1).getStatus();
            if (leftStatus == mEventModelList.get(position).getStatus()) {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_right_slot);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_slot);
            }
            return drawable;
        }


        // Rest Items
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

    private void changeTimeSlotBgColor(EventModel eventModel, int position, Drawable drawable) {

        if (!mItemViewList.isEmpty() && mItemViewList.get(position).isSelected()) {
            drawable.setColorFilter(eventModel.getEventColor(), PorterDuff.Mode.SRC_IN);
        }
        else {
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.timeSlotExpired), PorterDuff.Mode.SRC_IN);
        }
    }


    private class ItemView {
        private boolean isSelected;
        private float viewWidth;

        boolean isSelected() {
            return isSelected;
        }

        void setSelected(boolean selected) {
            isSelected = selected;
        }

        float getViewWidth() {
            return viewWidth;
        }

        void setViewWidth(float viewWidth) {
            this.viewWidth = viewWidth;
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
