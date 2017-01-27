package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.data.repository.datasource.impl.CalendarApiStoreFactory;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.domain.mapper.LocalEventMapper;
import com.badoo.meetingroom.data.repository.datasource.intf.CalendarApiStore;
import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.domain.mapper.RoomMapper;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class CalendarApiRepoImpl implements CalendarApiRepo {

    private final CalendarApiStore mCalendarApiStore;

    private final LocalEventMapper mLocalEventMapper;

    private final RoomMapper mRoomMapper;

    @Inject
    CalendarApiRepoImpl(CalendarApiStoreFactory eventDataStoreFactory,
                        LocalEventMapper eventDataMapper, RoomMapper roomMapper) {
        mLocalEventMapper = eventDataMapper;
        mCalendarApiStore = eventDataStoreFactory.createEventStore();
        mRoomMapper = roomMapper;
    }

    @Override
    public Observable<List<LocalEvent>> getEvents(CalendarApiParams params) {
        return mCalendarApiStore.getEventList(params).map(mLocalEventMapper::map);
    }

    @Override
    public Observable<Event> insertEvent(CalendarApiParams params) {
        return mCalendarApiStore.insertEvent(params);
    }

    @Override
    public Observable<Void> deleteEvent(CalendarApiParams params) {
        return mCalendarApiStore.deleteEvent(params);
    }

    @Override
    public Observable<Event> updateEvent(CalendarApiParams params) {
        return mCalendarApiStore.updateEvent(params);
    }

    @Override
    public Observable<List<Room>> getCalendarList() {
        return mCalendarApiStore.getCalendarList().map(mRoomMapper::map);
    }

    @Override
    public Observable<Channel> bindPushNotifications(CalendarApiParams params) {
        return mCalendarApiStore.bindPushNotifications(params);
    }

}
