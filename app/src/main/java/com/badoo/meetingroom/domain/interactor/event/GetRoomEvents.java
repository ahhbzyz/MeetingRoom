package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.LocalEventRepo;
import com.badoo.meetingroom.presentation.mapper.RoomEventsMapper;
import com.badoo.meetingroom.presentation.model.EventModel;


import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class GetRoomEvents extends UseCase<List<EventModel>> {

    public static final String NAME = "getRoomEvents";

    private final LocalEventRepo mRoomEventRepository;
    private final RoomEventsMapper mRoomEventModelMapper;
    private CalendarApiParams mParams;

    @Inject
    GetRoomEvents(LocalEventRepo roomEventRepository, RoomEventsMapper roomEventModelMapper) {
        mRoomEventRepository = roomEventRepository;
        mRoomEventModelMapper = roomEventModelMapper;
    }

    public GetRoomEvents init(CalendarApiParams params) {
        mParams = params;
        return this;
    }

    @Override
    protected Observable<List<EventModel>> buildUseCaseObservable() {
        if (mParams == null) {
            throw new IllegalArgumentException("init(CalendarApiParams) not called, or called with null argument");
        }
        return mRoomEventRepository.getRoomEventList(mParams).map(mRoomEventModelMapper::map);
    }
}
