package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.LocalEventRepo;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.mapper.CalendarEventsMapper;
import com.badoo.meetingroom.presentation.model.EventModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class GetCalendarEvents extends UseCase<List<EventModel>> {
    public static final String NAME = "getCalendarEvents";

    private final LocalEventRepo mRoomEventRepository;
    private final CalendarEventsMapper mCalendarEventsMapper;
    private CalendarApiParams mParams;

    @Inject
    GetCalendarEvents(LocalEventRepo roomEventRepository, CalendarEventsMapper calendarEventsMapper) {
        mRoomEventRepository = roomEventRepository;
        mCalendarEventsMapper = calendarEventsMapper;
    }

    public GetCalendarEvents init(CalendarApiParams params, int page) {
        mCalendarEventsMapper.setEventStartTime(Badoo.getStartTimeOfDay(page));
        mCalendarEventsMapper.setEventEndTime(Badoo.getEndTimeOfDay(page));
        mParams = params;
        return this;
    }

    @Override
    protected Observable<List<EventModel>> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mRoomEventRepository.getRoomEventList(mParams).map(mCalendarEventsMapper::map);
    }

}
