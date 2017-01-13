package com.badoo.meetingroom.presentation.presenter.impl;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.GetEvents;
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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@PerActivity
public class RoomStatusPresenterImpl implements RoomStatusPresenter {

    private RoomEventsView mRoomEventsView;

    private final GetEvents mGetEventsUseCase;
    private final InsertEvent mInsertEventUseCase;
    private final DeleteEvent mDeleteEventUseCase;
    private final UpdateEvent mUpdateEventUseCase;

    private final RoomEventModelMapper mMapper;

    //private LinkedList<RoomEventModel> mEventQueue;
    private List<RoomEventModel> mEventList;
    private int mCurrentEventPos;

    private RoomEventModel mCurrentEvent;
    private HashSet<String> mConfirmedIds;


    @Inject
    RoomStatusPresenterImpl(@Named(GetEvents.NAME) GetEvents getEventsUseCase,
                                   @Named(InsertEvent.NAME) InsertEvent insertEventUseCase,
                                   @Named(DeleteEvent.NAME) DeleteEvent deleteEventUseCase,
                                   @Named(UpdateEvent.NAME) UpdateEvent updateEventUseCase,
                                   RoomEventModelMapper mapper) {
        mGetEventsUseCase = getEventsUseCase;
        mInsertEventUseCase = insertEventUseCase;
        mDeleteEventUseCase = deleteEventUseCase;
        mUpdateEventUseCase = updateEventUseCase;
        mMapper = mapper;
        mConfirmedIds = new HashSet<>();
//        mEventQueue = new LinkedList<>();
        mEventList = new ArrayList<>();
    }

    @Override
    public void setView(@NonNull RoomEventsView roomEventsView) {
        mRoomEventsView = roomEventsView;
    }

    @Override
    public void onCountDownTicking(long millisUntilFinished) {
        if (mCurrentEvent.isBusy()) {
            if (mCurrentEvent.isOnHold()) {
                mRoomEventsView.updateCircleTimeViewTime(TimeHelper.formatMillisInMinAndSec(millisUntilFinished));
            } else {
                mRoomEventsView.updateCircleTimeViewTime(mCurrentEvent.getEndTimeInText());
            }
        } else {
            long hours = TimeUnit.MILLISECONDS.toHours(mCurrentEvent.getRemainingTime());
            if (hours >= 2) {
                mRoomEventsView.updateCircleTimeViewTime("2H+");
            } else {
                mRoomEventsView.updateCircleTimeViewTime(TimeHelper.formatMillisInMinAndSec(millisUntilFinished));
            }
        }
    }

    @Override
    public void onCountDownFinished() {
        if (mCurrentEvent.isProcessing() && mCurrentEvent.isBusy() && !mCurrentEvent.isConfirmed() && !mCurrentEvent.isOnHold()) {
            deleteEvent();
        } else {
            removeFirstEventFromQueue();
            showCurrentEventOnCircleTimeView();
            mRoomEventsView.updateHorizontalTimelinePosition(getNumOfExpiredEvents());
        }
    }

    @Override
    public void setDoNotDisturb(boolean isDoNotDisturb) {
        mCurrentEvent.setDoNotDisturb(isDoNotDisturb);
        if (mCurrentEvent.isDoNotDisturb()) {
            mRoomEventsView.hideTopBottomContent();
        } else {
            mRoomEventsView.showTopBottomContent();
        }
        mRoomEventsView.updateCircleTimeViewStatus(mCurrentEvent);
        showButtonsForEvent();
    }

    @Override
    public void onEventClicked(int position) {
        RoomEventModel event = mEventList.get(position);
        if (event.isAvailable()) {
            if (event.isProcessing()) {
                mRoomEventsView.bookRoom(TimeHelper.getCurrentTimeInMillis(), event.getEndTime());
            } else {
                mRoomEventsView.bookRoom(event.getStartTime(), event.getEndTime());
            }
        } else {
            mRoomEventsView.showEventOrganizerDialog(mCurrentEvent);
        }
    }

    @Override
    public void restartCountDown() {
        mRoomEventsView.restartCountDownTimer(mCurrentEvent);
    }

    @Override
    public void setEventConfirmed() {
        mCurrentEvent.setConfirmed(true);
        if (!mConfirmedIds.contains(mCurrentEvent.getId())) {
            mConfirmedIds.add(mCurrentEvent.getId());
        }
        showCurrentEventOnCircleTimeView();
        mRoomEventsView.updateHorizontalTimelinePosition(getNumOfExpiredEvents());
        mRoomEventsView.updateRecyclerView();
    }

    @Override
    public void onSystemTimeUpdate() {
        mRoomEventsView.updateHorizontalTimelineCurrentTime();
        mRoomEventsView.updateHorizontalTimelinePosition(getNumOfExpiredEvents());
        mRoomEventsView.updateRecyclerView();
    }

    @Override
    public void circleTimeViewBtnClick() {
        if (mCurrentEvent.isAvailable()) {
            mRoomEventsView.bookRoom(TimeHelper.getCurrentTimeInMillis(), mCurrentEvent.getEndTime());
        } else if (mCurrentEvent.isConfirmed()) {
            mRoomEventsView.showEventOrganizerDialog(mCurrentEvent);
        }
    }

    private void showButtonsForEvent() {
        switch (mCurrentEvent.getStatus()) {
            case RoomEventModelImpl.AVAILABLE:
                mRoomEventsView.showButtonGroupForAvailableStatus();
                break;
            case RoomEventModelImpl.BUSY:
                if (mCurrentEvent.isOnHold()) {
                    mRoomEventsView.showButtonGroupForOnHoldStatus();
                } else if (mCurrentEvent.isDoNotDisturb()) {
                    mRoomEventsView.showButtonGroupForDoNotDisturbStatus(mCurrentEvent.getEndTimeInText());
                } else {
                    mRoomEventsView.showButtonGroupForBusyStatus();
                }
                break;
            default:
                break;
        }
    }

    private void showCurrentEventOnCircleTimeView() {
        if (mEventList != null && mEventList.get(mCurrentEventPos) != null) {
            mCurrentEvent = mEventList.get(mCurrentEventPos);
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mCurrentEvent.setConfirmed(true);
            }
            this.mRoomEventsView.renderRoomEvent(mCurrentEvent);
            showButtonsForEvent();
        }
    }

    private void removeFirstEventFromQueue(){
        if (mEventList != null && !mEventList.isEmpty()) {
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mConfirmedIds.remove(mCurrentEvent.getId());
            }
            mCurrentEventPos ++;
        }
    }

    private void showEventsOnHorizontalTimelineView() {
        if (mEventList != null && !mEventList.isEmpty()) {
            mRoomEventsView.renderRoomEventList(mEventList);
            mRoomEventsView.updateHorizontalTimelinePosition(getNumOfExpiredEvents());
        }
    }

    private int getNumOfExpiredEvents(){
        return mCurrentEventPos;
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

        mGetEventsUseCase.init(event).execute(new GetEventsSubscriber());
    }
    
    @Override
    public void insertEvent(int bookingPeriod) {

        long startTime = TimeHelper.getCurrentTimeInMillis();
        long endTime = TimeHelper.getCurrentTimeInMillis() + TimeHelper.min2Millis(bookingPeriod);

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

            event.setDescription("fast_book");

            mInsertEventUseCase.init(event).execute(new InsertEventSubscriber());
        }
    }
    
    @Override
    public void deleteEvent() {
        if (mCurrentEvent.getId() != null) {
            Event event = new Event();
            event.setId(mCurrentEvent.getId());
            mDeleteEventUseCase.init(event).execute(new DeleteEventSubscriber());
        }
    }
    
    @Override
    public void updateEvent() {
        if (mEventList != null && mEventList.get(mCurrentEventPos + 1) != null) {
            if (mEventList.get(mCurrentEventPos + 1).isAvailable()) {
                long extendedTime = mEventList.get(mCurrentEventPos + 1).getDuration() >= TimeHelper.min2Millis(15) ? TimeHelper.min2Millis(15) : mEventList.get(mCurrentEventPos + 1).getDuration();
                // Extent
                Event event = new Event();

                event.setId(mCurrentEvent.getId());

                DateTime endDateTime = new DateTime(mCurrentEvent.getEndTime() + extendedTime);
                EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/London");
                event.setEnd(end);

                mUpdateEventUseCase.init(event).execute(new UpdateEventSubscriber());
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
            mCurrentEventPos = 0;
            mEventList.clear();
            mEventList.addAll(mEventModelList);
            for (RoomEventModel eventModel : mEventList) {
                if (eventModel.isExpired()) {
                    mCurrentEventPos++;
                } else {
                    break;
                }
            }
            showCurrentEventOnCircleTimeView();
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
            getEvents();
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
            getEvents();
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
            getEvents();
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
        mGetEventsUseCase.unSubscribe();
        mInsertEventUseCase.unSubscribe();
        mUpdateEventUseCase.unSubscribe();
        mDeleteEventUseCase.unSubscribe();
    }
}
