package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.remote.GoogleCalendarApi;
import com.badoo.meetingroom.data.remote.GoogleCalendarApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.RoomEventDataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
@Singleton
public class RoomEventDataStoreFactory {

    private final Context mContext;

    @Inject
    public RoomEventDataStoreFactory(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    public RoomEventDataStore createRemoteDataStore() {
        GoogleCalendarApi googleApi = new GoogleCalendarApiImpl(this.mContext);
        return new RemoteRoomEventDataStore(googleApi);
    }
}
