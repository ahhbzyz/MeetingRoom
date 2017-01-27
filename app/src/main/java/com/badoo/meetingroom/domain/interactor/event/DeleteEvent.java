package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class DeleteEvent extends UseCase {

    public static final String NAME = "deleteEvent";

    private final CalendarApiRepo mCalendarApiRepo;

    @Inject
    DeleteEvent(CalendarApiRepo calendarApiRepo) {
        mCalendarApiRepo = calendarApiRepo;
    }

    protected Observable<Void> buildUseCaseObservable(CalendarApiParams params) {
        if (params == null) {
            throw new IllegalArgumentException("execute(CalendarApiParams) not called, or called with null argument");
        }
        return mCalendarApiRepo.deleteEvent(params);

    }

    public void execute(Subscriber<Void> useCaseSubscriber, CalendarApiParams params) {
        mSubscription = buildUseCaseObservable(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
