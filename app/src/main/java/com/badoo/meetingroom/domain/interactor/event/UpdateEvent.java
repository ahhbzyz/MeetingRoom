package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class UpdateEvent extends UseCase {

    public static final String NAME = "updateEvent";

    private final CalendarApiRepo mCalendarApiRepo;

    @Inject
    UpdateEvent(CalendarApiRepo calendarApiRepo) {
        mCalendarApiRepo = calendarApiRepo;
    }

    private Observable<Event> buildUseCaseObservable(CalendarApiParams params) {
        if (params == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mCalendarApiRepo.updateEvent(params);
    }

    public void execute(Subscriber<Event> useCaseSubscriber, CalendarApiParams params) {
        mSubscription = buildUseCaseObservable(params)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
