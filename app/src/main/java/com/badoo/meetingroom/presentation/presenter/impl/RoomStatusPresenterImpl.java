package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.domain.interactor.InsertEvent;
import com.badoo.meetingroom.domain.interactor.UpdateEvent;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.model.RoomEventModelImpl;
import com.badoo.meetingroom.presentation.presenter.intf.RoomStatusPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@PerActivity
public class RoomStatusPresenterImpl implements RoomStatusPresenter {

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
    public RoomStatusPresenterImpl(@Named(GetRoomEventList.NAME) GetRoomEventList getRoomEventListUseCase,
                                   @Named(InsertEvent.NAME) InsertEvent insertEventUseCase,
                                   @Named(DeleteEvent.NAME) DeleteEvent deleteEventUseCase,
                                   @Named(UpdateEvent.NAME) UpdateEvent updateEventUseCase,
                                   RoomEventModelMapper mapper) {

        mGetRoomEventListUseCase = getRoomEventListUseCase;
        mInsertEventUseCase = insertEventUseCase;
        mDeleteEventUseCase = deleteEventUseCase;
        mUpdateEventUseCase = updateEventUseCase;
        mMapper = mapper;
        mConfirmedIds = new HashSet<>();
    }

    @Override
    public void setView(@NonNull RoomEventsView roomEventsView) {
        this.mRoomEventsView = roomEventsView;
    }

    @Override
    public void onCountDownTicking(long millisUntilFinished) {
        mRoomEventsView.updateCircleTimeView(mCurrentEvent);
    }

    @Override
    public void onCountDownFinished() {
        removeEventFromCircleTimeView();
        showEventOnCircleTimeView();
    }

    @Override
    public void setDoNotDisturb(boolean doNotDisturb) {
        mCurrentEvent.setDoNotDisturb(doNotDisturb);
        mRoomEventsView.updateCircleTimeView(mCurrentEvent);
        mRoomEventsView.updateHorizontalTimelineView(getNumOfExpiredEvents());
    }

    @Override
    public void setEventConfirmed() {
        mCurrentEvent.setOnHold(false);
        if (!mConfirmedIds.contains(mCurrentEvent.getId())) {
            mConfirmedIds.add(mCurrentEvent.getId());
        }
        mRoomEventsView.updateCircleTimeView(mCurrentEvent);
        mRoomEventsView.updateHorizontalTimelineView(getNumOfExpiredEvents());
        showButtonsForEvent();
    }

    @Override
    public void updateHorizontalTimeline() {
        mRoomEventsView.updateHorizontalTimelineView(getNumOfExpiredEvents());
    }

    @Override
    public void circleTimeViewBtnClick() {
        if (mCurrentEvent.isAvailable()) {
            mRoomEventsView.bookRoom(TimeHelper.getCurrentTimeInMillis(), mCurrentEvent.getEndTime());
        } else if (mCurrentEvent.isConfirmed()) {
            mRoomEventsView.showEventOrganizerDialog();
        }
    }

    private void showButtonsForEvent() {
        switch (mCurrentEvent.getStatus()) {
            case RoomEventModelImpl.AVAILABLE:
                mRoomEventsView.showButtonsForAvailableStatus();
                break;
            case RoomEventModelImpl.BUSY:
                if (mCurrentEvent.isOnHold()) {
                    mRoomEventsView.showButtonsForOnHoldStatus();
                } else if (mCurrentEvent.isDoNotDisturb()) {
                    mRoomEventsView.showButtonsForDoNotDisturbStatus(mCurrentEvent.getEndTimeInText());
                } else {
                    mRoomEventsView.showButtonsForBusyStatus();
                }
                break;
            default:
                break;
        }
    }

    private void showEventOnCircleTimeView() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            mCurrentEvent = mEventModelQueue.peek();
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mCurrentEvent.setOnHold(false);
            }
            this.mRoomEventsView.renderNextRoomEvent(mCurrentEvent);
            showButtonsForEvent();
        }
    }

    private void removeEventFromCircleTimeView(){
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mConfirmedIds.remove(mCurrentEvent.getId());
            }
            mEventModelQueue.remove();
        }
    }

    private void showEventsOnHorizontalTimelineView() {
        if (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            List<RoomEventModel> roomEventModelList = new ArrayList<>();
            roomEventModelList.addAll(mEventModelQueue);
            mRoomEventsView.renderRoomEvents(roomEventModelList);
        }
    }

    private int getNumOfExpiredEvents(){
        int mNumOfExpiredEvents = 0;
        while (mEventModelQueue != null && !mEventModelQueue.isEmpty()) {
            if (mEventModelQueue.peek().isExpired()) {
                mEventModelQueue.remove();
                mNumOfExpiredEvents++;
            }
            else {
                break;
            }
        }
        return mNumOfExpiredEvents;
    }

    @Override
    public void getEvents() {
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

        this.mGetRoomEventListUseCase.init(event).execute(new GetEventsSubscriber());
    }
    
    @Override
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
    public void deleteEvent() {
        Event event = new Event();
        event.setId(mCurrentEvent.getId());
        this.mDeleteEventUseCase.init(event).execute(new DeleteEventSubscriber());
    }
    
    @Override
    public void updateEvent() {
        if (mEventModelQueue != null && mEventModelQueue.size() >= 1) {
            if (mEventModelQueue.get(1).isAvailable()) {
                long extendedTime = mEventModelQueue.get(1).getDuration() >= TimeHelper.min2Millis(15) ? TimeHelper.min2Millis(15) : mEventModelQueue.get(1).getDuration();
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

    private final class GetEventsSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(mRoomEventsView.context().getString(R.string.loading) + "...");
        }

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            Collection<RoomEventModel> mEventModelList = mMapper.map(roomEvents);
            mEventModelQueue = new LinkedList<>();
            mEventModelQueue.addAll(mEventModelList);
            showEventOnCircleTimeView();
            showEventsOnHorizontalTimelineView();
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
            }
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.handleRecoverableAuthException(userRecoverableAuthIOException);
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }

    private final class InsertEventSubscriber extends DefaultSubscriber<Event> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(mRoomEventsView.context().getString(R.string.booking) + "...");
        }

        @Override
        public void onNext(Event event) {
            super.onNext(event);
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
            }
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.handleRecoverableAuthException(userRecoverableAuthIOException);
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private final class DeleteEventSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(mRoomEventsView.context().getString(R.string.canceling) + "...");
        }

        @Override
        public void onNext(Void aVoid) {
            super.onNext(aVoid);
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
            }
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.handleRecoverableAuthException(userRecoverableAuthIOException);
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private final class UpdateEventSubscriber extends DefaultSubscriber<Event> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(mRoomEventsView.context().getString(R.string.booking) + "...");
        }

        @Override
        public void onNext(Event event) {
            super.onNext(event);
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
            }
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomEventsView.handleRecoverableAuthException(userRecoverableAuthIOException);
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomEventsView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void showViewLoading(String message) {
        mRoomEventsView.showLoadingData(message);
    }

    private void dismissViewLoading() {
        mRoomEventsView.dismissLoadingData();
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
