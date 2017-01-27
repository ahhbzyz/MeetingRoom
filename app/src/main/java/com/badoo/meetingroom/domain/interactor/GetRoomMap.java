package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.mapper.RoomModelMapper;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;


import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class GetRoomMap extends UseCase {

    public static final String NAME = "getRoomMap";

    private final CalendarApiRepo mCalendarApiRepo;
    private final RoomModelMapper mRoomModelMapper;

    @Inject
    GetRoomMap(CalendarApiRepo calendarApiRepo, RoomModelMapper roomModelMapper) {
        mCalendarApiRepo = calendarApiRepo;
        mRoomModelMapper = roomModelMapper;
    }

    protected Observable<TreeMap<Integer, List<RoomModel>>> buildUseCaseObservable() {
       return mCalendarApiRepo.getCalendarList().map(mRoomModelMapper::mapToRoomMap);
    }

    public void execute(Subscriber<TreeMap<Integer, List<RoomModel>>> useCaseSubscriber) {
        mSubscription = buildUseCaseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}