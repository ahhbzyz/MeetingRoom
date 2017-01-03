package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.data.GetEventsParams;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.view.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;

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

    @Inject
    GoogleAccountCredential mCredential;

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

    public void showCurrentTimeMark() {
        long currentTime = TimeHelper.getCurrentTimeSinceMidNight();
        if (mDailyEventsView.getCurrentPage() == 0) {
            mDailyEventsView.showCurrentTimeMark(true, currentTime, TimeHelper.getCurrentTimeInMillisInText());
        } else {
            mDailyEventsView.showCurrentTimeMark(false, currentTime, TimeHelper.getCurrentTimeInMillisInText());
        }
    }

    public void loadRoomEventList() {
        this.getRoomEventList();
    }

    private void showDailyEventsInView(List<RoomEventModel> roomEventModelList) {
        mDailyEventsView.renderDailyEvents(roomEventModelList);
    }

    private void showViewLoading(boolean visibility) {
        this.mDailyEventsView.showLoadingData(visibility);
    }


    @Override
    public void setView(DailyEventsView dailyEventsView) {
        this.mDailyEventsView = dailyEventsView;
    }

    @Override
    public void updateCurrentTimeMark() {
        mDailyEventsView.updateCurrentTimeText(TimeHelper.getCurrentTimeInMillisInText());
        mDailyEventsView.updateCurrentTimeMarkPosition(TimeHelper.getCurrentTimeSinceMidNight());
    }

    @Override
    public void updateCurrentTimeMarkWhenScrolled(int dy) {
        mDailyEventsView.updateCurrentTimeMarkPosition(dy);
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
        DateTime start = new DateTime(TimeHelper.getMidNightTimeOfDay(mPage));
        DateTime end = new DateTime(TimeHelper.getMidNightTimeOfDay(mPage + 1));
        GetEventsParams params = new GetEventsParams.EventsParamsBuilder(mCredential)
            .startTime(start)
            .endTime(end)
            .build();
        mMapper.setEventStartTime(start.getValue());
        mMapper.setEventEndTime(end.getValue());
        this.getRoomEventListUseCase.init(params).execute(new RoomEventListSubscriber());
    }

    public void updateRecyclerView() {
        mDailyEventsView.updateDailyEventList();
    }


    private final class RoomEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(true);
        }

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            mEventList = mMapper.map(roomEvents);
            showDailyEventsInView(mEventList);
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
