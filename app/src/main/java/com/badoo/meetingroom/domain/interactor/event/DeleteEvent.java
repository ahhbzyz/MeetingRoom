package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class DeleteEvent extends UseCase<Void> {

    public static final String NAME = "deleteEvent";

    private final CalendarApiRepo mCalendarApiRepo;
    private CalendarApiParams mParams;

    @Inject
    DeleteEvent(CalendarApiRepo calendarApiRepo) {
        mCalendarApiRepo = calendarApiRepo;
    }

    public DeleteEvent init(CalendarApiParams params) {
        mParams = params;
        return this;
    }

    @Override
    protected Observable<Void> buildUseCaseObservable() {
        if (this.mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mCalendarApiRepo.deleteEvent(mParams);

    }
}
