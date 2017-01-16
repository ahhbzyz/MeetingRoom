package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.repository.RoomEventRepo;
import com.google.api.services.calendar.model.Event;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 02/01/2017.
 */

public class InsertEvent extends UseCase<Event> {

    public static final String NAME = "insertEvent";

    private final RoomEventRepo mRoomEventRepository;
    private Event mEventParams;

    @Inject
    InsertEvent(RoomEventRepo mRoomEventRepository) {
        this.mRoomEventRepository = mRoomEventRepository;
    }

    public InsertEvent init(Event event) {
        this.mEventParams = event;
        return this;
    }

    @Override
    protected Observable<Event> buildUseCaseObservable() {
        if (this.mEventParams == null) {
            throw new IllegalArgumentException("init(EventInsertParams) not called, or called with null argument");
        }
        return Observable.concat(validate(), mRoomEventRepository.insertEvent(mEventParams));

    }

    private Observable<Event> validate() {
        return Observable.create(subscriber -> {
            if (InsertEvent.this.mEventParams.getStart().getDateTime() == null ||
                InsertEvent.this.mEventParams.getEnd().getDateTime() == null) {

            } else {
                subscriber.onCompleted();
            }
        });
    }
}
