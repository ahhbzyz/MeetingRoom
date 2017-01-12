package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetEvents;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public class DailyEventsPresenterImpl implements DailyEventsPresenter {


    private DailyEventsView mDailyEventsView;
    private final GetEvents mGetEventsUseCase;
    private final RoomEventModelMapper mMapper;
    private List<RoomEventModel> mEventList;
    private int mPage = 0;

    @Inject
    DailyEventsPresenterImpl(@Named(GetEvents.NAME)GetEvents getEventsUseCase,
                                    RoomEventModelMapper mapper) {
        mGetEventsUseCase = getEventsUseCase;
        mMapper = mapper;
        mEventList = new ArrayList<>();
    }

    private void showDailyEventsInView(List<RoomEventModel> roomEventModelList) {
        mDailyEventsView.renderDailyEvents(roomEventModelList);

    }

    @Override
    public void updateCurrentTimeLayout() {
        mDailyEventsView.updateCurrentTimeLayout(getNumOfExpiredEvents());
    }

    @Override
    public void setView(DailyEventsView dailyEventsView) {
        mDailyEventsView = dailyEventsView;
        mPage = mDailyEventsView.getCurrentPage();
    }

    @Override
    public void onEventClicked(int position) {
        if (mEventList != null && mEventList.get(position).isAvailable()) {
            RoomEventModel event = mEventList.get(position);
            long startTime = event.isProcessing() ? TimeHelper.getCurrentTimeInMillis() : event.getStartTime();
            mDailyEventsView.bookRoom(startTime, event.getEndTime());
        }
    }


    // TODO efficient way
    private int getNumOfExpiredEvents() {
        int numOfExpiredEvents = 0;
        for (RoomEventModel event: mEventList) {
            if(event.isExpired()) {
                numOfExpiredEvents++;
            } else {
                break;
            }
        }
        return numOfExpiredEvents;
    }

    @Override
    public void getEvents() {
        Event event = new Event();

        DateTime startDateTime = new DateTime(Badoo.getStartTimeOfDay(mPage));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        event.setStart(start);

        DateTime endDateTime = new DateTime(Badoo.getEndTimeOfDay(mPage));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);

        mMapper.setEventStartTime(startDateTime.getValue());
        mMapper.setEventEndTime(endDateTime.getValue());
        mGetEventsUseCase.init(event).execute(new GetEventsSubscriber());
    }

    private final class GetEventsSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onStart() {
            super.onStart();
            mDailyEventsView.showLoadingData("");
        }

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            mEventList = mMapper.map(roomEvents);
            updateCurrentTimeLayout();
            showDailyEventsInView(mEventList);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mDailyEventsView.dismissLoadingData();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mDailyEventsView.dismissLoadingData();
            try {
                throw e;
            } catch (UserRecoverableAuthIOException e1) {
                mDailyEventsView.handlerUserRecoverableAuth(e1);
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
        mGetEventsUseCase.unSubscribe();
    }
}
