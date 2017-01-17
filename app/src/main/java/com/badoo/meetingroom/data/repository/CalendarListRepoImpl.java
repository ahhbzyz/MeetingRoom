package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.impl.CalendarListStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.CalendarListStore;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.domain.mapper.RoomMapper;
import com.badoo.meetingroom.domain.repository.CalendarListRepo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

@Singleton
public class CalendarListRepoImpl implements CalendarListRepo {

    private final CalendarListStore mCalendarListStore;
    private final RoomMapper mRoomMapper;

    @Inject
    CalendarListRepoImpl(CalendarListStoreFactory calendarListStoreFactory, RoomMapper roomMapper) {
        mCalendarListStore = calendarListStoreFactory.createCalendarListStore();
        mRoomMapper = roomMapper;
    }

    @Override
    public Observable<List<Room>> getCalendarList() {
        return mCalendarListStore.getCalendarList().map(mRoomMapper::map);
    }
}
