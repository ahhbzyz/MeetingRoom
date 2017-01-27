package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.local.FileManager;
import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApi;
import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.CalendarApiStore;
import com.google.api.services.calendar.Calendar;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
@Singleton
public class CalendarApiStoreFactory {

    private final Context mContext;
    private final Calendar mServices;
    private final FileManager mFileManger;
    
    @Inject
    CalendarApiStoreFactory(@NonNull Context context, @NonNull Calendar services, FileManager fileManger) {
        mContext = context.getApplicationContext();
        mServices = services;
        mFileManger = fileManger;
    }

    public CalendarApiStore createEventStore() {
        GoogleCalendarApi googleApi = new GoogleCalendarApiImpl(mContext, mServices, mFileManger);
        return new CalendarApiStoreImpl(googleApi);
    }
}
