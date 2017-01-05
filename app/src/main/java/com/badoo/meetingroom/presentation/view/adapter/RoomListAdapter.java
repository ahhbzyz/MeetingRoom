package com.badoo.meetingroom.presentation.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.Room;

import java.util.List;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {

    private List<Room> mRoomList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public RoomListAdapter() {

    }

    public void setRoomList(List<Room> roomList) {
        this.mRoomList = roomList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return mRoomList.size();
    }
}
