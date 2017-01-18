package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.data.repository.datasource.impl.EventStoreFactory;
import com.badoo.meetingroom.domain.mapper.LocalEventMapper;
import com.badoo.meetingroom.data.repository.datasource.intf.EventStore;
import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.domain.repository.LocalEventRepo;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class LocalEventRepoImpl implements LocalEventRepo {

    private LocalEventMapper mLocalEventMapper;
    private final EventStore mEventDataStore;

    @Inject
    LocalEventRepoImpl(EventStoreFactory eventDataStoreFactory,
                       LocalEventMapper eventDataMapper) {
        mLocalEventMapper = eventDataMapper;
        mEventDataStore = eventDataStoreFactory.createEventStore();
    }

    @Override
    public Observable<List<LocalEvent>> getRoomEventList(CalendarApiParams params) {
        return mEventDataStore.getEventList(params).map(mLocalEventMapper::map);
    }

    @Override
    public Observable<Event> insertEvent(CalendarApiParams params) {
        return mEventDataStore.insertEvent(params);
    }

    @Override
    public Observable<Void> deleteEvent(CalendarApiParams params) {
        return mEventDataStore.deleteEvent(params);
    }

    @Override
    public Observable<Event> updateEvent(CalendarApiParams params) {
        return mEventDataStore.updateEvent(params);
    }
}
