package com.badoo.meetingroom.data.repository.datasource;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.remote.GoogleApi;
import com.badoo.meetingroom.data.remote.GoogleApiImpl;

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
        GoogleApi googleApi = new GoogleApiImpl(this.mContext);
        return new RemoteRoomEventDataStore(googleApi);
    }
}
