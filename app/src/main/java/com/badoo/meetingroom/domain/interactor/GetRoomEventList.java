package com.badoo.meetingroom.domain.interactor;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.GetEventsParams;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;


import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class GetRoomEventList extends UseCaseWithParams<List<RoomEvent>, GetEventsParams> {

    public static final String NAME = "getRoomEventList";

    private final RoomEventRepository mRoomEventRepository;
    private GetEventsParams mParams;

    @Inject
    GetRoomEventList(RoomEventRepository roomEventRepository) {
        this.mRoomEventRepository = roomEventRepository;

    }

    public GetRoomEventList init(GetEventsParams params) {
        this.mParams = params;
        return this;
    }

    @Override
    protected Observable<List<RoomEvent>> buildUseCaseObservable() {
        if (this.mParams == null) {
            throw new IllegalArgumentException("init(GetEventsParams) not called, or called with null argument");
        }
        return Observable.concat(validate(), mRoomEventRepository.getRoomEventList(mParams));
    }


    private Observable<List<RoomEvent>> validate() {
        return Observable.create(subscriber -> {
            if (GetRoomEventList.this.mParams.getCredential() == null) {

            } else {
                subscriber.onCompleted();
            }
        });
    }
}
