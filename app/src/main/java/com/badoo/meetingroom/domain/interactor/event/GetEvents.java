package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.LocalEventRepo;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.mapper.EventMapper;
import com.badoo.meetingroom.presentation.model.intf.EventModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class GetEvents extends UseCase<List<EventModel>> {
    public static final String NAME = "getEvents";

    private final LocalEventRepo mRoomEventRepository;
    private final EventMapper mEventMapper;
    private CalendarApiParams mParams;

    @Inject
    GetEvents(LocalEventRepo roomEventRepository, EventMapper eventMapper) {
        mRoomEventRepository = roomEventRepository;
        mEventMapper = eventMapper;
    }

    public GetEvents init(CalendarApiParams params, int page) {
        mEventMapper.setEventStartTime(Badoo.getStartTimeOfDay(page));
        mEventMapper.setEventEndTime(Badoo.getEndTimeOfDay(page));
        mParams = params;
        return this;
    }

    @Override
    protected Observable<List<EventModel>> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mRoomEventRepository.getRoomEventList(mParams).map(mEventMapper::map);
    }

}
