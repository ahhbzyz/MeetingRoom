package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApi;
import com.badoo.meetingroom.data.repository.datasource.intf.CalendarListStore;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

class CalendarListStoreImpl implements CalendarListStore {

    private final GoogleCalendarApi mGoogleCalendarApi;

    CalendarListStoreImpl(GoogleCalendarApi googleCalendarApi) {
        this.mGoogleCalendarApi = googleCalendarApi;
    }

    @Override
    public Observable<List<CalendarListEntry>> getCalendarList() {
        return mGoogleCalendarApi.getCalendarList();
    }
}
