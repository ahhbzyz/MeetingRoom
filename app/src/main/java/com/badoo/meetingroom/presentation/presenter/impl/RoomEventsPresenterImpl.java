package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomEventsPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

    private RoomEventModel mCurrentEvent;

    @Inject
    GoogleAccountCredential mCredential;

    @Inject
    RoomEventsPresenterImpl(@Named(GetRoomEventList.NAME) GetRoomEventList getRoomEventListUseCase,
                            RoomEventModelMapper mapper) {
        this.getRoomEventListUseCase = getRoomEventListUseCase;
        this.mMapper = mapper;
    }

    @Override
    public void setView(@NonNull RoomEventsView roomEventsView) {
        this.mRoomEventsView = roomEventsView;
    }


    @Override
    public void onCountDownTicking(long millisUntilFinished) {
        if (mCurrentEvent.isConfirmed()) {
            mRoomEventsView.setCircleTimeViewTime(mCurrentEvent.getEndTimeInText());
        } else {
            long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
            if (hours >= 2) {
                mRoomEventsView.setCircleTimeViewTime("2H+");
            } else {
                mRoomEventsView.setCircleTimeViewTime(TimeHelper.formatMillisInMinsAndSecs(millisUntilFinished));
            }
        }

    }

    @Override
    public void onCountDownFinished() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            // Remove last one
            mEventModelQueue.remove();
            if (!mEventModelQueue.isEmpty()) {
                mCurrentEvent = mEventModelQueue.peek();
                mRoomEventsView.renderNextRoomEvent(mCurrentEvent);
                showButtonsForEvent();
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

    private void loadRoomEventList() {
        this.showViewRetry(false);
        this.showViewLoading(true);
        this.getRoomEventList();
    }

    private void showViewLoading(boolean visibility) {
        this.mRoomEventsView.showLoadingData(visibility);
    }

    private void showButtonsForEvent(){
        mRoomEventsView.clearAllButtonsInLayout();
        switch (mCurrentEvent.getStatus()) {
            case RoomEventModel.AVAILABLE:
                mRoomEventsView.showButtonsInAvailableStatus();
                break;
            case RoomEventModel.BUSY:
                if (mCurrentEvent.isOnHold()) {
                    mRoomEventsView.showButtonsInOnHoldStatus();
                } else if (mCurrentEvent.isDoNotDisturb()) {
                    mRoomEventsView.showButtonsInDoNotDisturbStatus(mCurrentEvent.getEndTimeInText());
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
            mCurrentEvent = mEventModelQueue.peek();
            this.mRoomEventsView.renderNextRoomEvent(mCurrentEvent);
            showButtonsForEvent();
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
        String currentTime = TimeHelper.getCurrentTimeInMillisInText();
        mRoomEventsView.setCurrentTimeText(currentTime);
    }

    private void getRoomEventList() {
        DateTime start = new DateTime(TimeHelper.getMidNightTimeOfDay(0));
        DateTime end = new DateTime(TimeHelper.getMidNightTimeOfDay(1));
        EventsParams params = new EventsParams.EventsParamsBuilder(mCredential)
            .startTime(start)
            .endTime(end)
            .build();

        mMapper.setEventStartTime(start.getValue());
        mMapper.setEventEndTime(end.getValue());
        this.getRoomEventListUseCase.init(params).execute(new RoomEventListSubscriber());
    }

    @Override
    public RoomEventModel getCurrentEvent() {
        return mCurrentEvent;
    }


    @Override
    public void confirmEvent() {
        mCurrentEvent.setOnHold(false);
        mRoomEventsView.updateEventStatus();
        showButtonsForEvent();
    }

    @Override
    public void dismissEvent() {
        //TODO
    }

    @Override
    public void setDoNotDisturb() {
        mCurrentEvent.setDoNotDisturb(true);
        mRoomEventsView.updateEventStatus();
        showButtonsForEvent();
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
