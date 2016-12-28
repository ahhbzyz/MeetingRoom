package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public class DailyEventsPresenterImpl implements DailyEventsPresenter {


    private DailyEventsView mDailyEventsView;

    private final GetRoomEventList getRoomEventListUseCase;

    private final RoomEventModelMapper mMapper;

    private int mPage = 0;

    @Inject
    GoogleAccountCredential mCredential;

    @Inject
    public DailyEventsPresenterImpl(@Named(GetRoomEventList.NAME)GetRoomEventList getRoomEventListUseCase,
                                    RoomEventModelMapper mMapper) {
        this.getRoomEventListUseCase = getRoomEventListUseCase;
        this.mMapper = mMapper;
    }



    @Override
    public void init() {
        mPage = mDailyEventsView.getCurrentPage();
        loadRoomEventList();
        setUpDailyEventList();

    }



    private void loadRoomEventList() {
        this.showViewLoading(true);
        this.getRoomEventList();
    }

    private void setUpDailyEventList() {
        mDailyEventsView.setUpRecyclerView();
    }

    private void showViewLoading(boolean visibility) {
        this.mDailyEventsView.showLoadingData(visibility);
    }


    @Override
    public void setView(DailyEventsView dailyEventsView) {
        this.mDailyEventsView = dailyEventsView;
    }

    public void getRoomEventList() {
        DateTime start = new DateTime(TimeHelper.getMidNightTimeOfDay(mPage));
        DateTime end = new DateTime(TimeHelper.getMidNightTimeOfDay(2));
        EventsParams params = new EventsParams.EventsParamsBuilder(mCredential)
            .startTime(start)
            .endTime(end)
            .build();
        mMapper.setStartDateTime(start.getValue());
        this.getRoomEventListUseCase.init(params).execute(new RoomEventListSubscriber());
    }

    private final class RoomEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            Collection<RoomEventModel> mEventModelList = mMapper.map(roomEvents);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            showViewLoading(false);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            showViewLoading(false);
        }
    }

    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {
        getRoomEventListUseCase.unSubscribe();
    }
}
