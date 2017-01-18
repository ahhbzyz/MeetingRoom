package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.model.RoomModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {


    private List<RoomModel> mRoomList;
    private Context mContext;
    private int lastPosition = -1;

    public void setRoomList(List<RoomModel> roomList) {
        mRoomList = roomList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_remaining_time) TextView mRemainingTimeTv;
        @BindView(R.id.tv_room_name) TextView mRoomNameTv;
        @BindView(R.id.tv_room_info) TextView mRoomInfo;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Inject
    RoomListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomModel roomModel = mRoomList.get(position);
        holder.mRoomNameTv.setText(roomModel.getName());
        if (roomModel.getCurrentEvent() != null) {

            RoomEventModel currentEvent = roomModel.getCurrentEvent();
            long remainingHours = TimeUnit.MILLISECONDS.toHours(currentEvent.getRemainingTime());
            if (remainingHours >= 2) {
                holder.mRemainingTimeTv.setText(mContext.getString(R.string.two_hour_plus));
            } else {
                holder.mRemainingTimeTv.setText(TimeHelper.formatMillisInMin(currentEvent.getRemainingTime()));
            }
            if (currentEvent.isAvailable()) {

                holder.mRemainingTimeTv.setBackground(mContext.getDrawable(R.drawable.bg_oval_available));

                if (remainingHours >= 2) {
                    holder.mRoomInfo.setText(mContext.getString(R.string.available_for) + " " + mContext.getString(R.string.two_hour_plus));
                } else {
                    holder.mRoomInfo.setText(mContext.getString(R.string.available_for) + " " + TimeHelper.formatMillisInMin(currentEvent.getRemainingTime()) + " min");
                }
            } else {
                holder.mRemainingTimeTv.setBackground(mContext.getDrawable(R.drawable.bg_oval_busy));
                holder.mRoomInfo.setText(mContext.getString(R.string.busy_until) + " " + currentEvent.getEndTimeInText());
            }
        }
        //setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mRoomList == null ? 0 : mRoomList.size();
    }
}
