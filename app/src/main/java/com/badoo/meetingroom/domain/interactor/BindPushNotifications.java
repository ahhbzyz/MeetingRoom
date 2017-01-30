package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.google.api.services.calendar.model.Channel;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 25/01/2017.
 */

public class BindPushNotifications extends UseCase {

    public static final String NAME = "bindPushNotifications";

    private final CalendarApiRepo mCalendarApiRepo;

    @Inject
    BindPushNotifications(CalendarApiRepo calendarApiRepo) {
        this.mCalendarApiRepo = calendarApiRepo;
    }

    private Observable<Channel> buildUseCaseObservable(CalendarApiParams params) {
        if (params == null) {
            throw new IllegalArgumentException("init(PersonId) not called, or called with null argument");
        }
        return mCalendarApiRepo.bindPushNotifications(params);
    }

    public void execute(Subscriber<Channel> useCaseSubscriber, CalendarApiParams params) {
        mSubscription = buildUseCaseObservable(params)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
