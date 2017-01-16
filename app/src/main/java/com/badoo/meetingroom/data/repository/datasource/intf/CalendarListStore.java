package com.badoo.meetingroom.data.repository.datasource.intf;

import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public interface CalendarListStore {
    Observable<List<CalendarListEntry>> getCalendarList();
}
