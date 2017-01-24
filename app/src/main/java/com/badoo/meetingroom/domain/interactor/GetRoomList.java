package com.badoo.meetingroom.domain.interactor;

import android.util.SparseArray;

import com.badoo.meetingroom.domain.repository.CalendarListRepo;
import com.badoo.meetingroom.presentation.mapper.RoomModelMapper;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class GetRoomList extends UseCase<TreeMap<Integer, List<RoomModel>>> {

    public static final String NAME = "getRoomList";

    private final CalendarListRepo mCalendarListRepo;
    private final RoomModelMapper mRoomModelMapper;

    @Inject
    GetRoomList(CalendarListRepo calendarListRepo, RoomModelMapper roomModelMapper) {
        mCalendarListRepo = calendarListRepo;
        mRoomModelMapper = roomModelMapper;
    }

    @Override
    protected Observable<TreeMap<Integer, List<RoomModel>>> buildUseCaseObservable() {
        return mCalendarListRepo.getCalendarList().map(mRoomModelMapper::map);
    }
}
