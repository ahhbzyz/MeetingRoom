package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 25/01/2017.
 */

public class BindPushNotifications extends UseCase<Void> {

    public static final String NAME = "bindPushNotifications";

    private final CalendarApiRepo mCalendarApiRepo;
    private CalendarApiParams mParams;

    public BindPushNotifications init(CalendarApiParams params) {
        mParams = params;
        return this;
    }

    @Inject
    BindPushNotifications(CalendarApiRepo calendarApiRepo) {
        this.mCalendarApiRepo = calendarApiRepo;
    }

    @Override
    protected Observable<Void> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(PersonId) not called, or called with null argument");
        }
        return mCalendarApiRepo.bindPushNotifications(mParams);
    }
}
