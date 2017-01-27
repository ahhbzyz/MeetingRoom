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
 * Created by zhangyaozhong on 02/01/2017.
 */

public class InsertEvent extends UseCase {

    public static final String NAME = "insertEvent";

    private final CalendarApiRepo mCalendarApiRepo;

    @Inject
    InsertEvent(CalendarApiRepo calendarApiRepo) {
        mCalendarApiRepo = calendarApiRepo;
    }



    private Observable<Event> buildUseCaseObservable(CalendarApiParams params) {
        if (params == null) {
            throw new IllegalArgumentException("execute(params) not called, or called with null argument");
        }
        return mCalendarApiRepo.insertEvent(params);

    }

    public void execute(Subscriber<Event> useCaseSubscriber, CalendarApiParams params) {
        mSubscription = buildUseCaseObservable(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
