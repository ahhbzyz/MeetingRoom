package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.repository.RoomEventRepository;
import com.google.api.services.calendar.model.Event;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class DeleteEvent extends UseCase<Void> {

    public static final String NAME = "deleteEvent";

    private final RoomEventRepository mRoomEventRepository;
    private Event mEventParams;

    @Inject
    DeleteEvent(RoomEventRepository mRoomEventRepository) {
        this.mRoomEventRepository = mRoomEventRepository;
    }

    public DeleteEvent init(Event event) {
        this.mEventParams = event;
        return this;
    }

    @Override
    protected Observable<Void> buildUseCaseObservable() {
        if (this.mEventParams == null) {
            throw new IllegalArgumentException("init(EventInsertParams) not called, or called with null argument");
        }
        return Observable.concat(validate(), mRoomEventRepository.deleteEvent(mEventParams));

    }

    private Observable<Void> validate() {
        return Observable.create(subscriber -> {
            if (DeleteEvent.this.mEventParams.getId() == null) {

            } else {
                subscriber.onCompleted();
            }
        });
    }
}
