package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomEventsPresenter;
import com.badoo.meetingroom.presentation.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeUtils;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventsPresenterImpl implements RoomEventsPresenter {

    private RoomEventsView mRoomEventsView;

    private final GetRoomEventList getRoomEventListUseCase;

    private final RoomEventModelMapper mMapper;

    @Inject
    GoogleAccountCredential mCredential;

    @Inject
    RoomEventsPresenterImpl(@Named(GetRoomEventList.NAME) GetRoomEventList getRoomEventListUseCase, RoomEventModelMapper mapper) {
        this.getRoomEventListUseCase = getRoomEventListUseCase;
        this.mMapper = mapper;
    }

    @Override
    public void setView(@NonNull RoomEventsView roomEventsView) {
        this.mRoomEventsView = roomEventsView;
    }

    @Override
    public void onCountDownTicking() {

    }

    @Override
    public void onCountDownFinished() {

    }

    @Override
    public void init() {
        loadRoomEventList();
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

    private void loadRoomEventList() {
        this.showViewRetry(false);
        this.showViewLoading(true);
        this.getRoomEventList();
    }

    private void showViewLoading(boolean visibility) {
        this.mRoomEventsView.showLoadingData(visibility);
    }


    private void showViewRetry(boolean visibility) {
        this.mRoomEventsView.showRetryLoading(visibility);
    }



    private void showRoomEventsInView(List<RoomEvent> roomEventList) {
        final Collection<RoomEventModel> roomEventModelList =
            this.mMapper.map(roomEventList);

        LinkedList<RoomEventModel> roomEventModelQueue = new LinkedList<>();
        roomEventModelQueue.addAll(roomEventModelList);

        this.mRoomEventsView.renderRoomEvents(roomEventModelQueue);
    }

    private void getRoomEventList() {
        DateTime start = new DateTime(TimeUtils.getMidNightTimeOfDay(0));
        DateTime end = new DateTime(TimeUtils.getMidNightTimeOfDay(2));
        EventsParams params = new EventsParams.EventsParamsBuilder(mCredential)
            .startTime(start)
            .endTime(end)
            .build();

        this.getRoomEventListUseCase.init(params).execute(new RoomEventListSubscriber());
    }


    private final class RoomEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            showRoomEventsInView(roomEvents);
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
            //show error message;
            showViewRetry(true);
        }
    }
}
