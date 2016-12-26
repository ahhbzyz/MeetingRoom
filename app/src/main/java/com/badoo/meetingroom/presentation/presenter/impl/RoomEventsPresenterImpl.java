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
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventsPresenterImpl implements RoomEventsPresenter {

    private RoomEventsView mRoomEventsView;

    private final GetRoomEventList getRoomEventListUseCase;

    private final RoomEventModelMapper mMapper;

    private LinkedList<RoomEventModel> mEventModelQueue;


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
    public void onCountDownTicking(long millisUntilFinished) {
        mRoomEventsView.setCircleTimeViewTime(millisUntilFinished);
    }

    @Override
    public void onCountDownFinished() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            // Remove last one
            mEventModelQueue.remove();
            if (!mEventModelQueue.isEmpty()) {
                RoomEventModel eventModel = mEventModelQueue.peek();
                mRoomEventsView.renderNextRoomEvent(eventModel);
                showButtonsForEvent(mEventModelQueue.peek());
            }
        }
    }

    @Override
    public void init() {
        loadRoomEventList();
        setUpCircleTimeView();
        setUpHorizontalTimelineView();
        updateCurrentTimeForHtv();
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

    private void showButtonsForEvent(RoomEventModel eventModel){
        mRoomEventsView.clearAllButtonsInLayout();
        switch (eventModel.getStatus()) {
            case RoomEventModel.AVAILABLE:
                mRoomEventsView.showButtonsInAvailableStatus();
                break;
            case RoomEventModel.BUSY:
                if (eventModel.isOnHold()) {
                    mRoomEventsView.showButtonsInOnHoldStatus();
                } else if (eventModel.isDoNotDisturb()) {
                    mRoomEventsView.showButtonsInDoNotDisturbStatus();
                } else {
                    mRoomEventsView.showButtonsInBusyStatus();
                }
                break;
        }
    }

    private void showViewRetry(boolean visibility) {
        this.mRoomEventsView.showRetryLoading(visibility);
    }


    private void setUpCircleTimeView() {
        mRoomEventsView.setUpCircleTimeView();
    }

    private void showFirstEventOnCircleTimeView() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            this.mRoomEventsView.renderNextRoomEvent(mEventModelQueue.peek());
            showButtonsForEvent(mEventModelQueue.peek());
        }
    }

    private void setUpHorizontalTimelineView() {
        mRoomEventsView.setUpHorizontalTimelineView();
    }

    private void showEventsOnHorizontalTimelineView() {
        if(mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            mRoomEventsView.renderRoomEvents(mEventModelQueue);
        }
    }

    @Override
    public void updateCurrentTimeForHtv() {
        String currentTime = TimeUtils.formatTime(TimeUtils.getCurrentTimeInMillis());
        mRoomEventsView.setCurrentTime(currentTime);
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
            Collection<RoomEventModel> mEventModelList = mMapper.map(roomEvents);
            mEventModelQueue = new LinkedList<>();
            mEventModelQueue.addAll(mEventModelList);
            showFirstEventOnCircleTimeView();
            showEventsOnHorizontalTimelineView();
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
