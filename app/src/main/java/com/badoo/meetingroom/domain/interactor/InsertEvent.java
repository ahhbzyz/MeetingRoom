package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.data.InsertEventParams;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 02/01/2017.
 */

public class InsertEvent extends UseCaseWithParams<Event, InsertEventParams> {

    public static final String NAME = "insertEvent";

    private final RoomEventRepository mRoomEventRepository;
    private InsertEventParams mParams;

    @Inject
    InsertEvent(RoomEventRepository mRoomEventRepository) {
        this.mRoomEventRepository = mRoomEventRepository;
    }

    public InsertEvent init(InsertEventParams params) {
        this.mParams = params;
        return this;
    }

    @Override
    protected Observable<Event> buildUseCaseObservable() {
        if (this.mParams == null) {
            throw new IllegalArgumentException("init(InsertEventParams) not called, or called with null argument");
        }
        return Observable.concat(validate(), mRoomEventRepository.insertEvent(mParams));

    }

    private Observable<Event> validate() {
        return Observable.create(subscriber -> {
            if (InsertEvent.this.mParams.getCredential() == null) {

            } else {
                subscriber.onCompleted();
            }
        });
    }
}
