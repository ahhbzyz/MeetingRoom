package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.RoomEventRepo;
import com.google.api.services.calendar.model.Event;


import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class GetEvents extends UseCase<List<RoomEvent>> {

    public static final String NAME = "getEvents";

    private final RoomEventRepo mRoomEventRepository;
    private Event mEventParams;
    private String mRoomId;

    @Inject
    GetEvents(RoomEventRepo roomEventRepository) {
        this.mRoomEventRepository = roomEventRepository;
    }

    public GetEvents init(String roomId, Event event) {
        this.mRoomId = roomId;
        this.mEventParams = event;
        return this;
    }

    @Override
    protected Observable<List<RoomEvent>> buildUseCaseObservable() {
        if (this.mEventParams == null) {
            throw new IllegalArgumentException("init(EventsGetParams) not called, or called with null argument");
        }
        return mRoomEventRepository.getRoomEventList(mRoomId, mEventParams);
    }
}
