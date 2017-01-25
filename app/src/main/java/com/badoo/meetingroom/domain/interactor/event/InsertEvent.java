package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.google.api.services.calendar.model.Event;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 02/01/2017.
 */

public class InsertEvent extends UseCase<Event> {

    public static final String NAME = "insertEvent";

    private final CalendarApiRepo mCalendarApiRepo;
    private CalendarApiParams mParams;

    @Inject
    InsertEvent(CalendarApiRepo calendarApiRepo) {
        mCalendarApiRepo = calendarApiRepo;
    }

    public InsertEvent init(CalendarApiParams params) {
        mParams = params;
        return this;
    }

    @Override
    protected Observable<Event> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mCalendarApiRepo.insertEvent(mParams);

    }
}
