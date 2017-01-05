package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.domain.interactor.InsertEvent;
import com.badoo.meetingroom.domain.interactor.UpdateEvent;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.model.RoomEventModelImpl;
import com.badoo.meetingroom.presentation.presenter.intf.RoomEventsPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Collection;
import java.util.HashSet;
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

    private final GetRoomEventList mGetRoomEventListUseCase;
    private final InsertEvent mInsertEventUseCase;
    private final DeleteEvent mDeleteEventUseCase;
    private final UpdateEvent mUpdateEventUseCase;

    private final RoomEventModelMapper mMapper;

    private LinkedList<RoomEventModel> mEventModelQueue;

    private RoomEventModel mCurrentEvent;
    private HashSet<String> mConfirmedIds;


    @Inject
    RoomEventsPresenterImpl(@Named(GetRoomEventList.NAME) GetRoomEventList getRoomEventListUseCase,
                            @Named(InsertEvent.NAME) InsertEvent insertEventUseCase,
                            @Named(DeleteEvent.NAME) DeleteEvent deleteEventUseCase,
                            @Named(UpdateEvent.NAME) UpdateEvent updateEventUseCase,
                            RoomEventModelMapper mapper) {
        this.mGetRoomEventListUseCase = getRoomEventListUseCase;
        this.mInsertEventUseCase = insertEventUseCase;
        this.mDeleteEventUseCase = deleteEventUseCase;
        this.mUpdateEventUseCase = updateEventUseCase;
        this.mMapper = mapper;
        mConfirmedIds = new HashSet<>();
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
                mRoomEventsView.setCircleTimeViewTime(TimeHelper.formatMillisInMinAndSec(millisUntilFinished));
            }
        }

    }

    @Override
    public void onCountDownFinished() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            // Remove last one
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mConfirmedIds.remove(mCurrentEvent.getId());
            }
            mEventModelQueue.remove();
            if (!mEventModelQueue.isEmpty()) {
                mCurrentEvent = mEventModelQueue.peek();
                if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                    mCurrentEvent.setOnHold(false);
                }
                mRoomEventsView.renderNextRoomEvent(mCurrentEvent);
                showButtonsForEvent();
            }
        }
    }

    @Override
    public void init() {
        loadRoomEventList();
    }

    private void loadRoomEventList() {
        this.getRoomEventList();
    }

    private void showViewLoading(boolean visibility) {
        this.mRoomEventsView.showLoadingData(visibility);
    }

    private void showButtonsForEvent(){
        mRoomEventsView.clearAllButtonsInLayout();
        switch (mCurrentEvent.getStatus()) {
            case RoomEventModelImpl.AVAILABLE:
                mRoomEventsView.showButtonsInAvailableStatus();
                break;
            case RoomEventModelImpl.BUSY:
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

    private void showFirstEventOnCircleTimeView() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            mCurrentEvent = mEventModelQueue.peek();
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mCurrentEvent.setOnHold(false);
            }
            this.mRoomEventsView.renderNextRoomEvent(mCurrentEvent);
            showButtonsForEvent();
        }
    }

    private void showEventsOnHorizontalTimelineView() {
        if(mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            mRoomEventsView.renderRoomEvents(mEventModelQueue);
        }
    }

    private void getRoomEventList() {
        Event event = new Event();
        DateTime startDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(0));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        event.setStart(start);

        DateTime endDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(1));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);

        mMapper.setEventStartTime(startDateTime.getValue());
        mMapper.setEventEndTime(endDateTime.getValue());

        this.mGetRoomEventListUseCase.init(event).execute(new RoomEventListSubscriber());
    }

    public void insertEvent(int min) {
        long startTime = TimeHelper.getCurrentTimeInMillis();
        long endTime = TimeHelper.getCurrentTimeInMillis() + TimeHelper.min2Millis(min);

        if (mCurrentEvent.isAvailable() && endTime <= mCurrentEvent.getEndTime()) {
            Event event = new Event();
            DateTime startDateTime = new DateTime(startTime);
            EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/London");
            event.setStart(start);

            DateTime endDateTime = new DateTime(endTime);
            EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/London");
            event.setEnd(end);
            this.mInsertEventUseCase.init(event).execute(new InsertEventSubscriber());
        }
    }

    @Override
    public void confirmEvent() {
        mCurrentEvent.setOnHold(false);
        if (!mConfirmedIds.contains(mCurrentEvent.getId())) {
            mConfirmedIds.add(mCurrentEvent.getId());
        }
        mRoomEventsView.updateEventStatus();
        showButtonsForEvent();
    }

    @Override
    public void deleteEvent() {
        Event event = new Event();
        event.setId(mCurrentEvent.getId());
        this.mDeleteEventUseCase.init(event).execute(new DeleteEventSubscriber());
    }

    @Override
    public void setDoNotDisturb(boolean doNotDisturb) {
        if (mCurrentEvent != null) {
            mCurrentEvent.setDoNotDisturb(doNotDisturb);
            mRoomEventsView.updateEventStatus();
            showButtonsForEvent();
        }
    }

    @Override
    public void circleTimeViewBtnClick() {
        if (mCurrentEvent.isAvailable()
            && (mCurrentEvent.getEndTime() - TimeHelper.getCurrentTimeInMillis()) >= TimeHelper.min2Millis(15)) {
            mRoomEventsView.bookRoom(TimeHelper.getCurrentTimeInMillis(), mCurrentEvent.getEndTime());
        } else {
            mRoomEventsView.showEventOrganizerDialog();
        }
    }

    @Override
    public void extendBookingPeriod() {
        if (mEventModelQueue!=null && mEventModelQueue.size() >= 1) {
            if (mEventModelQueue.get(1).isAvailable()) {
                long extendedTime = mEventModelQueue.get(1).getDuration() >= TimeHelper.min2Millis(15) ? TimeHelper.min2Millis(15)  : mEventModelQueue.get(1).getDuration();
                // Extent
                Event event = new Event();
                event.setId(mCurrentEvent.getId());
                DateTime endDateTime = new DateTime(mCurrentEvent.getEndTime() + extendedTime);
                EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/London");
                event.setEnd(end);
                this.mUpdateEventUseCase.init(event).execute(new UpdateEventSubscriber());
            }
        }
    }


    private final class RoomEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(true);
        }

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
            try {
                throw e;
            } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.showRecoverableAuth(userRecoverableAuthIOException);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }

    private final class InsertEventSubscriber extends DefaultSubscriber<Event> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(true);
        }

        @Override
        public void onNext(Event event) {
            super.onNext(event);
            if (event != null){
                mRoomEventsView.showEventInsertSuccessful();
            }
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
            } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.showRecoverableAuth(userRecoverableAuthIOException);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private final class DeleteEventSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(true);
        }

        @Override
        public void onNext(Void aVoid) {
            super.onNext(aVoid);
            mRoomEventsView.showEventDeleteSuccessful();
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
            } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.showRecoverableAuth(userRecoverableAuthIOException);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private final class UpdateEventSubscriber extends DefaultSubscriber<Event> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(true);
        }

        @Override
        public void onNext(Event event) {
            super.onNext(event);
            if (event != null) {
                mRoomEventsView.showEventExtendSuccessful();
            }
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
            } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.showRecoverableAuth(userRecoverableAuthIOException);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    @Override
    public void Resume() {}

    @Override
    public void Pause() {}

    @Override
    public void destroy() {
        mGetRoomEventListUseCase.unSubscribe();
    }
}
