package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.LocalEventRepo;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.mapper.EventsMapper;
import com.badoo.meetingroom.presentation.model.EventModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class GetEvents extends UseCase<List<EventModel>> {
    public static final String NAME = "getCalendarEvents";

    private final LocalEventRepo mRoomEventRepository;
    private final EventsMapper mEventsMapper;
    private CalendarApiParams mParams;

    @Inject
    GetEvents(LocalEventRepo roomEventRepository, EventsMapper eventsMapper) {
        mRoomEventRepository = roomEventRepository;
        mEventsMapper = eventsMapper;
    }

    public GetEvents init(CalendarApiParams params, int page) {
        mEventsMapper.setEventStartTime(Badoo.getStartTimeOfDay(page));
        mEventsMapper.setEventEndTime(Badoo.getEndTimeOfDay(page));
        mParams = params;
        return this;
    }

    @Override
    protected Observable<List<EventModel>> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mRoomEventRepository.getRoomEventList(mParams).map(mEventsMapper::map);
    }

}
