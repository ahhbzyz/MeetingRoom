package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.EventParams;
import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.domain.mapper.RoomEventDataMapper;
import com.badoo.meetingroom.data.repository.datasource.RoomEventDataStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.RoomEventDataStore;
import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class RoomEventDataRepository implements RoomEventRepository {

    private RoomEventDataMapper mRoomEventDataMapper;
    private final RoomEventDataStore mRoomEventDataStore;

    @Inject
    public RoomEventDataRepository(RoomEventDataStoreFactory roomEventDataStoreFactory,
                                   RoomEventDataMapper roomEventDataMapper) {
        this.mRoomEventDataMapper = roomEventDataMapper;
        this.mRoomEventDataStore = roomEventDataStoreFactory.createRemoteDataStore();
    }

    @Override
    public Observable<List<RoomEvent>> getRoomEventList(EventsParams params) {

        return mRoomEventDataStore.getEventList(params).map(this.mRoomEventDataMapper::transform);
    }

    @Override
    public Observable<Event> insertUserEvent(EventParams params) {
        return mRoomEventDataStore.insertEvent(params);
    }
}
