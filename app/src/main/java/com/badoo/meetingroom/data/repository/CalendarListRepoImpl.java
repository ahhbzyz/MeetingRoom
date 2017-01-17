package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.domain.repository.CalendarListRepo;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class CalendarListRepoImpl implements CalendarListRepo {
    @Override
    public Observable<List<CalendarListEntry>> getCalendarList() {
        return null;
    }
}
