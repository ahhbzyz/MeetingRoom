package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.view.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

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

    private List<RoomEventModel> mEventList;

    private int numOfExpiredEvents;

    @Inject
    DailyEventsPresenterImpl(@Named(GetRoomEventList.NAME)GetRoomEventList getRoomEventListUseCase,
                                    RoomEventModelMapper mMapper) {
        this.getRoomEventListUseCase = getRoomEventListUseCase;
        this.mMapper = mMapper;
    }

    @Override
    public void init() {
        mPage = mDailyEventsView.getCurrentPage();
        loadRoomEventList();
    }

    public void loadRoomEventList() {
        this.getRoomEventList();
    }

    private void showDailyEventsInView(List<RoomEventModel> roomEventModelList) {
        updateNumOfExpiredEvents();
        mDailyEventsView.renderDailyEvents(roomEventModelList);
    }

    private void showViewLoading() {
        this.mDailyEventsView.showLoadingData("");
    }

    private void dismissViewLoading() {
        this.mDailyEventsView.dismissLoadingData();
    }

    @Override
    public void setView(DailyEventsView dailyEventsView) {
        this.mDailyEventsView = dailyEventsView;
    }

    @Override
    public void onEventClicked(int position) {
        if (mEventList != null && mEventList.get(position).isAvailable()) {
            RoomEventModel event = mEventList.get(position);
            long startTime = event.isProcessing() ? TimeHelper.getCurrentTimeInMillis() : event.getStartTime();
            mDailyEventsView.bookRoom(startTime, event.getEndTime());
        }
    }

    private void getRoomEventList() {

        Event event = new Event();
        DateTime startDateTime = new DateTime(Badoo.getStartTimeOfDay(mPage));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        event.setStart(start);

        DateTime endDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(mPage + 1));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);


        mMapper.setEventStartTime(startDateTime.getValue());
        mMapper.setEventEndTime(endDateTime.getValue());
        this.getRoomEventListUseCase.init(event).execute(new RoomEventListSubscriber());
    }

    public float getNumOfExpiredEvents() {
        return numOfExpiredEvents;
    }

    public void updateNumOfExpiredEvents() {
        numOfExpiredEvents = 0;
        for (RoomEventModel event: mEventList) {
            if(event.isExpired()) {
                numOfExpiredEvents++;
            } else {
                break;
            }
        }
    }

    private final class RoomEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading();
        }

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            mEventList = mMapper.map(roomEvents);
            showDailyEventsInView(mEventList);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            dismissViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            dismissViewLoading();
            try {
                throw e;
            } catch (UserRecoverableAuthIOException e1) {
                mDailyEventsView.showUserRecoverableAuth(e1);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mDailyEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mDailyEventsView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
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
