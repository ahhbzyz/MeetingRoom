package com.badoo.meetingroom.domain.interactor.event;

import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.RoomEventRepo;
import com.google.api.services.calendar.model.Event;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class UpdateEvent extends UseCase<Event> {

    public static final String NAME = "updateEvent";

    private final RoomEventRepo mRoomEventRepository;
    private Event mEventParams;
    private String mRoomId;

    @Inject
    UpdateEvent(RoomEventRepo mRoomEventRepository) {
        this.mRoomEventRepository = mRoomEventRepository;
    }

    public UpdateEvent init(Event event) {
        this.mEventParams = event;
        return this;
    }

    @Override
    protected Observable<Event> buildUseCaseObservable() {
        if (this.mEventParams == null) {
            throw new IllegalArgumentException("init(EventInsertParams) not called, or called with null argument");
        }
        return mRoomEventRepository.updateEvent(mEventParams);
    }
}
