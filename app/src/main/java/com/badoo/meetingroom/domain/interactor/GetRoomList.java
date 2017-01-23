package com.badoo.meetingroom.domain.interactor;

import android.util.SparseArray;

import com.badoo.meetingroom.domain.repository.CalendarListRepo;
import com.badoo.meetingroom.presentation.mapper.RoomListMapper;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class GetRoomList extends UseCase<SparseArray<List<RoomModel>>> {

    public static final String NAME = "getRoomList";

    private final CalendarListRepo mCalendarListRepo;
    private final RoomListMapper mRoomListMapper;

    @Inject
    GetRoomList(CalendarListRepo calendarListRepo, RoomListMapper roomListMapper) {
        mCalendarListRepo = calendarListRepo;
        mRoomListMapper = roomListMapper;
    }

    @Override
    protected Observable<SparseArray<List<RoomModel>>> buildUseCaseObservable() {
        return mCalendarListRepo.getCalendarList().map(mRoomListMapper::map);
    }
}
