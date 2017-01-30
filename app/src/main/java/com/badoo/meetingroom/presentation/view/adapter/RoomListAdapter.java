package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
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
    private final float IMG_DISABLE_ALPHA = .3f;
    private OnItemClickListener mOnItemClickListener;

    public void setRoomList(List<RoomModel> roomList) {
        mRoomList = roomList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_remaining_time) TextView mRemainingTimeTv;

        @BindView(R.id.tv_room_name) TextView mRoomNameTv;

        @BindView(R.id.tv_room_info) TextView mRoomInfo;

        @BindView(R.id.tv_room_capacity) TextView mRoomCapacityTv;

        @BindView(R.id.img_tv) ImageView mTvImg;

        @BindView(R.id.img_video) ImageView mVideoImg;

        @BindView(R.id.img_beverage) ImageView mBeverageImg;

        @BindView(R.id.img_stationery) ImageView mStationeryImg;


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

        holder.mRoomCapacityTv.setText(String.valueOf(roomModel.getCapacity()));

        if (roomModel.isTvSupported()) {
            holder.mTvImg.setAlpha(1f);
        } else {
            holder.mTvImg.setAlpha(IMG_DISABLE_ALPHA);
        }

        if (roomModel.isVideoConferenceSupported()) {
            holder.mVideoImg.setAlpha(1f);
        } else {
            holder.mVideoImg.setAlpha(IMG_DISABLE_ALPHA);
        }


        if (roomModel.isBeverageAllowed()) {
            holder.mBeverageImg.setAlpha(1f);
        } else {
            holder.mBeverageImg.setAlpha(IMG_DISABLE_ALPHA);
        }

        if (roomModel.isStationerySupported()) {
            holder.mStationeryImg.setAlpha(1f);
        } else {
            holder.mStationeryImg.setAlpha(IMG_DISABLE_ALPHA);
        }


        if (roomModel.getCurrentEvent() == null) {
            return;
        }

        EventModel currentEvent = roomModel.getCurrentEvent();

        long remainingTime = currentEvent.getRemainingTimeUntilNextBusyEvent();

        if(position == 0) {
            System.out.println(TimeHelper.formatTime(currentEvent.getNextBusyEventStartTime()));

        }

        long remainingHours = TimeUnit.MILLISECONDS.toHours(remainingTime);

        if (remainingHours >= 2) {
            holder.mRemainingTimeTv.setText(mContext.getString(R.string.two_hour_plus));
        } else {

            holder.mRemainingTimeTv.setText(TimeHelper.formatMillisInMin(remainingTime + TimeHelper.min2Millis(1)));
        }

        if (currentEvent.isAvailable()) {

            holder.mRemainingTimeTv.setBackground(mContext.getDrawable(R.drawable.bg_oval_available));

            if (remainingHours >= 2) {
                holder.mRoomInfo.setText(mContext.getString(R.string.available_for) + " " + mContext.getString(R.string.two_hour_plus));
            } else {
                String remainingMins = (TimeHelper.formatMillisInMin(remainingTime + TimeHelper.min2Millis(1)));
                holder.mRoomInfo.setText(mContext.getString(R.string.available_for) + " " + remainingMins + " min");
            }
        } else {
            holder.mRemainingTimeTv.setBackground(mContext.getDrawable(R.drawable.bg_oval_busy));
            holder.mRoomInfo.setText(mContext.getString(R.string.busy_until) + " " + currentEvent.getEndTimeInText());
        }

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(position));
    }


    @Override
    public int getItemCount() {
        return mRoomList == null ? 0 : mRoomList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
