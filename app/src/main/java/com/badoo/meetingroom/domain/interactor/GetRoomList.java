package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.mapper.EventModelMapper;
import com.badoo.meetingroom.presentation.mapper.RoomModelMapper;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
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
 * Created by zhangyaozhong on 27/01/2017.
 */

public class GetRoomList extends UseCase {


    public static final String NAME = "getRoomList";

    private final CalendarApiRepo mCalendarApiRepo;
    private final RoomModelMapper mRoomModelMapper;
    private final EventModelMapper mEventModelMapper;

    @Inject
    GetRoomList(CalendarApiRepo calendarApiRepo, RoomModelMapper roomModelMapper, EventModelMapper eventModelMapper) {
        mCalendarApiRepo = calendarApiRepo;
        mRoomModelMapper = roomModelMapper;
        mEventModelMapper = eventModelMapper;
    }


    private Observable<List<EventModel>> getEvents(String roomId) {

        Event event = new Event();
        DateTime startDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(0));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(1));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime);
        event.setEnd(end);

        CalendarApiParams params = new CalendarApiParams(roomId);
        params.setEventParams(event);

        mEventModelMapper.setEventStartTime(Badoo.getStartTimeOfDay(0));
        mEventModelMapper.setEventEndTime(Badoo.getEndTimeOfDay(0));

        return mCalendarApiRepo.getEvents(params).map(mEventModelMapper::map);
    }

    private Observable<List<RoomModel>> buildUseCaseObservable(boolean loadRoomEvents) {

        if (loadRoomEvents) {
            return mCalendarApiRepo.getCalendarList().map(mRoomModelMapper::mapToRoomList)
                .flatMapIterable(list -> list)
                .doOnNext(
                    item -> getEvents(item.getId()).subscribe(item::setEventModelList)
                ).toList();

        } else {
            return mCalendarApiRepo.getCalendarList().map(mRoomModelMapper::mapToRoomList);
        }
    }

    public void execute(Subscriber<List<RoomModel>> useCaseSubscriber, Boolean loadRoomEvents) {
        mSubscription = buildUseCaseObservable(loadRoomEvents)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
