package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

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
    private List<String> slotStartTimeList;
    private final long mDefaultSlotLength = 15 * 60 * 1000;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_start_time) TextView mStartTimeTv;
        @BindView(R.id.img_time_slot) ImageView mTimeSlotImg;
        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Inject
    TimeSlotsAdapter(Context context) {
        this.mContext = context;
        slotStartTimeList = new ArrayList<>();
    }

    public void setTimeSlots(long startTime, long endTime) {

        if (endTime < startTime || (endTime - startTime) < mDefaultSlotLength) {
            throw new IllegalArgumentException("End time cannot less than start time " +
                "or time period cannot less than 15 minutes");
        }
        int remainderOfStartTimeMin = getRemainderOfMin(startTime);
        int startOffset = remainderOfStartTimeMin > 5 ? 10 : 5;
        long newStartTime = startTime + (startOffset - remainderOfStartTimeMin) * 60 * 1000;

        int numOfSlots = (int) ((endTime - newStartTime) / mDefaultSlotLength);
        System.out.println(numOfSlots);


        slotStartTimeList.add(TimeHelper.formatTime(startTime));
        if (numOfSlots <= 0) {
            slotStartTimeList.add(TimeHelper.formatTime(endTime));
        } else {
            for (int i = 0; i < numOfSlots; i++) {
                newStartTime += mDefaultSlotLength;
                slotStartTimeList.add(TimeHelper.formatTime(newStartTime));
            }
            if (newStartTime < endTime) {
                slotStartTimeList.add(TimeHelper.formatTime(endTime));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mStartTimeTv.setText(slotStartTimeList.get(position));
    }

    @Override
    public int getItemCount() {
        return slotStartTimeList.size();
    }

    private int getRemainderOfMin(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int min = calendar.get(Calendar.MINUTE);
        return min % 10;
    }
}
