package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.mapper.EventModelMapper;
import com.badoo.meetingroom.presentation.model.intf.EventModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class GetEvents extends UseCase<List<EventModel>> {
    public static final String NAME = "getEvents";

    private final CalendarApiRepo mCalendarApiRepository;
    private final EventModelMapper mEventModelMapper;
    private CalendarApiParams mParams;

    @Inject
    GetEvents(CalendarApiRepo calendarApiRepo, EventModelMapper eventModelMapper) {
        mCalendarApiRepository = calendarApiRepo;
        mEventModelMapper = eventModelMapper;
    }

    // Todo to execute method
    public GetEvents init(CalendarApiParams params, long startTime, long endTime) {
        mEventModelMapper.setEventStartTime(startTime);
        mEventModelMapper.setEventEndTime(endTime);
        mParams = params;
        return this;
    }

    @Override
    protected Observable<List<EventModel>> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mCalendarApiRepository.getRoomEventList(mParams).map(mEventModelMapper::map);
    }

}
