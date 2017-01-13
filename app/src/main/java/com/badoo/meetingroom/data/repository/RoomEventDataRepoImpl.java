package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.domain.mapper.RoomEventDataMapper;
import com.badoo.meetingroom.data.repository.datasource.impl.EventDataStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.EventDataStore;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.repository.RoomEventDataRepo;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class RoomEventDataRepoImpl implements RoomEventDataRepo {

    private RoomEventDataMapper mRoomEventDataMapper;
    private final EventDataStore mRoomEventDataStore;

    @Inject
    RoomEventDataRepoImpl(EventDataStoreFactory roomEventDataStoreFactory,
                          RoomEventDataMapper roomEventDataMapper) {
        this.mRoomEventDataMapper = roomEventDataMapper;
        this.mRoomEventDataStore = roomEventDataStoreFactory.createRemoteEventDataStore();
    }

    @Override
    public Observable<List<RoomEvent>> getRoomEventList(Event event) {
        return mRoomEventDataStore.getEventList(event).map(this.mRoomEventDataMapper::map);
    }

    @Override
    public Observable<Event> insertEvent(Event event) {
        return mRoomEventDataStore.insertEvent(event);
    }

    @Override
    public Observable<Void> deleteEvent(Event event) {
        return mRoomEventDataStore.deleteEvent(event);
    }

    @Override
    public Observable<Event> updateEvent(Event event) {
        return mRoomEventDataStore.updateEvent(event);
    }
}
