package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.remote.GoogleCalendarApi;
import com.badoo.meetingroom.data.remote.GoogleCalendarApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.RoomEventDataStore;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
@Singleton
public class RoomEventDataStoreFactory {

    private final Context mContext;
    private final Calendar mServices;
    
    @Inject
    RoomEventDataStoreFactory(@NonNull Context context, @NonNull Calendar services) {
        this.mContext = context.getApplicationContext();
        mServices = services;
    }

    public RoomEventDataStore createRemoteDataStore() {
        GoogleCalendarApi googleApi = new GoogleCalendarApiImpl(this.mContext, this.mServices);
        return new RemoteRoomEventDataStore(googleApi);
    }
}
