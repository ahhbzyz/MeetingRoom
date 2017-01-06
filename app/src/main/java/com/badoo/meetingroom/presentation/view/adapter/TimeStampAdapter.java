package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 06/01/2017.
 */

public class TimeStampAdapter extends RecyclerView.Adapter<TimeStampAdapter.ViewHolder> {

    private final Context mContext;
    private List<String> mTimeStampList;
    private final float mWidthPerMillis = DailyEventsAdapter.WIDTH_PER_MILLIS;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_time_stamp) TextView mTimeStampTv;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TimeStampAdapter(Context context) {
        this.mContext = context;
        this.mTimeStampList = new ArrayList<>();

        for (long i = Badoo.START_TIME; i <= Badoo.END_TIME; i += TimeHelper.hr2Millis(1)) {
            mTimeStampList.add(TimeHelper.formatTime(i));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_time_stamp, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewHeight =  (int) (TimeHelper.hr2Millis(1) * mWidthPerMillis);

        RelativeLayout.LayoutParams params = new
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, viewHeight);
        holder.itemView.setLayoutParams(params);

        holder.mTimeStampTv.setText(mTimeStampList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTimeStampList.size();
    }


}
