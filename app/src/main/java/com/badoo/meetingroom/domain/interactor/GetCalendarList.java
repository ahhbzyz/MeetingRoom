package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.domain.repository.CalendarListRepo;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class GetCalendarList extends UseCase<List<Room>> {

    public static final String NAME = "getCalendarList";

    private final CalendarListRepo mCalendarListRepo;

    @Inject
    GetCalendarList(CalendarListRepo calendarListRepo) {
        mCalendarListRepo = calendarListRepo;
    }

    @Override
    protected Observable<List<Room>> buildUseCaseObservable() {
        return mCalendarListRepo.getCalendarList();
    }
}
