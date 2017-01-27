package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.mapper.EventModelMapper;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class GetEvents extends UseCase {

    public static final String NAME = "getEvents";

    private final CalendarApiRepo mCalendarApiRepository;
    private final EventModelMapper mEventModelMapper;

    @Inject
    GetEvents(CalendarApiRepo calendarApiRepo, EventModelMapper eventModelMapper) {
        mCalendarApiRepository = calendarApiRepo;
        mEventModelMapper = eventModelMapper;
    }

    private Observable<List<EventModel>> buildUseCaseObservable(String roomId, int day) {

        if (roomId == null) {
            throw new IllegalArgumentException("execute(params) not called, or called with null argument");
        }

        Event event = new Event();
        DateTime startDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(day));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(day + 1));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime);
        event.setEnd(end);

        CalendarApiParams params = new CalendarApiParams(roomId);
        params.setEventParams(event);

        mEventModelMapper.setEventStartTime(Badoo.getStartTimeOfDay(day));
        mEventModelMapper.setEventEndTime(Badoo.getEndTimeOfDay(day));

        return mCalendarApiRepository.getEvents(params).map(mEventModelMapper::map);
    }

    public void execute(Subscriber<List<EventModel>> useCaseSubscriber, String roomId, int day) {
        mSubscription = buildUseCaseObservable(roomId, day)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
