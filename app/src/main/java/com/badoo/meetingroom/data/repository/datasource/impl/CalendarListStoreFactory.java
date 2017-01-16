package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApi;
import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.CalendarListStore;
import com.google.api.services.calendar.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

@Singleton
public class CalendarListStoreFactory {
    private final Context mContext;
    private final Calendar mServices;

    @Inject
    CalendarListStoreFactory(@NonNull Context context, @NonNull Calendar services) {
        mContext = context.getApplicationContext();
        mServices = services;
    }

    public CalendarListStore createRemoteEventDataStore() {
        GoogleCalendarApi googleApi = new GoogleCalendarApiImpl(this.mContext, this.mServices);
        return new CalendarListStoreImpl(googleApi);
    }
}
