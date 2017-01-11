package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;
import com.google.api.services.calendar.model.Event;


import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class GetEvents extends UseCase<List<RoomEvent>> {

    public static final String NAME = "getRoomEventList";

    private final RoomEventRepository mRoomEventRepository;
    private Event mEventParams;

    @Inject
    GetEvents(RoomEventRepository roomEventRepository) {
        this.mRoomEventRepository = roomEventRepository;

    }

    public GetEvents init(Event event) {
        this.mEventParams = event;
        return this;
    }

    @Override
    protected Observable<List<RoomEvent>> buildUseCaseObservable() {
        if (this.mEventParams == null) {
            throw new IllegalArgumentException("init(EventsGetParams) not called, or called with null argument");
        }
        return Observable.concat(validate(), mRoomEventRepository.getRoomEventList(mEventParams));
    }


    private Observable<List<RoomEvent>> validate() {
        return Observable.create(subscriber -> {
            if (GetEvents.this.mEventParams.getStart().getDateTime() == null ||
                GetEvents.this.mEventParams.getEnd().getDateTime() == null) {

            } else {
                subscriber.onCompleted();
            }
        });
    }
}
