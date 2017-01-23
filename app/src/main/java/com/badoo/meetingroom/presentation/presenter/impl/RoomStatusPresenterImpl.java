package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.event.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.event.GetEvents;
import com.badoo.meetingroom.domain.interactor.event.InsertEvent;
import com.badoo.meetingroom.domain.interactor.event.UpdateEvent;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomStatusPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomStatusView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@PerActivity
public class RoomStatusPresenterImpl implements RoomStatusPresenter {

    private RoomStatusView mRoomEventsView;

    private final GetEvents mGetEventsUseCase;
    private final InsertEvent mInsertEventUseCase;
    private final DeleteEvent mDeleteEventUseCase;
    private final UpdateEvent mUpdateEventUseCase;


    private List<EventModel> mEventList;
    private int mCurrentEventPos = -1;

    private EventModel mCurrentEvent;
    private HashSet<String> mConfirmedIds;


    @Inject
    RoomStatusPresenterImpl(@Named(GetEvents.NAME) GetEvents getEventsUseCase,
                            @Named(InsertEvent.NAME) InsertEvent insertEventUseCase,
                            @Named(DeleteEvent.NAME) DeleteEvent deleteEventUseCase,
                            @Named(UpdateEvent.NAME) UpdateEvent updateEventUseCase) {
        mGetEventsUseCase = getEventsUseCase;
        mInsertEventUseCase = insertEventUseCase;
        mDeleteEventUseCase = deleteEventUseCase;
        mUpdateEventUseCase = updateEventUseCase;
        mConfirmedIds = new HashSet<>();
        mEventList = new ArrayList<>();
    }

    @Override
    public void setView(@NonNull RoomStatusView roomEventsView) {
        mRoomEventsView = roomEventsView;
    }

    @Override
    public void onCountDownTicking() {
        mRoomEventsView.updateRoomStatusTextView(mCurrentEvent);
    }

    @Override
    public void onCountDownFinished() {
        if (mCurrentEvent.isProcessing() && mCurrentEvent.isBusy() && !mCurrentEvent.isConfirmed() && !mCurrentEvent.isOnHold()) {
            deleteEvent();
        }
        else {
            moveToNextEvent();
            showCurrentEventOnCircleTimeView();
            updateHorizontalTimeline();
        }
    }

    @Override
    public void updateHorizontalTimeline() {
        mRoomEventsView.updateHorizontalTimeline();
    }

    @Override
    public void setDoNotDisturb(boolean isDoNotDisturb) {
        mCurrentEvent.setDoNotDisturb(isDoNotDisturb);
        if (mCurrentEvent.isDoNotDisturb()) {
            mRoomEventsView.hideTopBottomContent();
        }
        else {
            mRoomEventsView.showTopBottomContent();
        }
        showCurrentEventOnCircleTimeView();
    }

    @Override
    public void setEventConfirmed() {
        mCurrentEvent.setConfirmed(true);
        if (!mConfirmedIds.contains(mCurrentEvent.getId())) {
            mConfirmedIds.add(mCurrentEvent.getId());
        }
        showCurrentEventOnCircleTimeView();
        updateHorizontalTimeline();
    }

    @Override
    public void onCircleTimeViewBtnClick() {
        if (mCurrentEvent.isAvailable()) {
            mRoomEventsView.bookRoom(TimeHelper.getCurrentTimeInMillis(), mCurrentEvent.getEndTime());
        }
        else {
            mRoomEventsView.showEventOrganizerDialog(mCurrentEvent);
        }
    }

    @Override
    public void onRestart() {
        mCurrentEventPos = getCurrentEventPosition(mEventList);
        showCurrentEventOnCircleTimeView();
        onSystemTimeRefresh();
    }

    @Override
    public void onSystemTimeRefresh() {
        updateHorizontalTimeline();
        checkEventExtendable();
    }

    private void showCurrentEventOnCircleTimeView() {

        if (mEventList != null && mCurrentEventPos >= 0 && mCurrentEventPos < mEventList.size() && mEventList.get(mCurrentEventPos) != null) {

            mCurrentEvent = mEventList.get(mCurrentEventPos);

            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mCurrentEvent.setConfirmed(true);
            }

            mRoomEventsView.renderRoomEvent(mCurrentEvent);
            checkEventExtendable();
        }
    }

    private void moveToNextEvent() {
        if (mEventList != null && !mEventList.isEmpty()) {
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mConfirmedIds.remove(mCurrentEvent.getId());
            }
            mCurrentEventPos++;
        }
    }

    private void showEventsOnHorizontalTimelineView() {
        if (mEventList != null && !mEventList.isEmpty()) {
            mRoomEventsView.renderRoomEventList(mEventList);
        }
    }

    private void checkEventExtendable() {
        if (mCurrentEvent != null &&
            mCurrentEvent.isBusy() &&
            mCurrentEvent.isProcessing() &&
            mEventList != null &&
            mCurrentEventPos < mEventList.size() - 1
            && mEventList.get(mCurrentEventPos + 1) != null) {

            EventModel eventModel = mEventList.get(mCurrentEventPos + 1);
            if (eventModel.isAvailable() && (eventModel.getNextBusyEventStartTime() - eventModel.getStartTime() >= TimeHelper.min2Millis(15))) {
                mRoomEventsView.updateExtendButtonState(true);
                return;
            }
        }
        mRoomEventsView.updateExtendButtonState(false);
    }

    private int getCurrentEventPosition(List<EventModel> roomEventModelList) {
        for (EventModel eventModel : roomEventModelList) {
            if (eventModel.isProcessing()) {
                return roomEventModelList.indexOf(eventModel);
            }
        }
        return -1;
    }

    private int getNumOfExpiredEvents() {
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

        if (Badoo.getCurrentRoom() != null) {
            CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
            params.setEventParams(event);
            long startTime = TimeHelper.getMidNightTimeOfDay(0);
            long endTime = TimeHelper.getMidNightTimeOfDay(1);
            mGetEventsUseCase.init(params, startTime, endTime).execute(new GetEventsSubscriber());
        }
    }

    @Override
    public void insertEvent(int bookingPeriod) {
        long startTime = TimeHelper.getCurrentTimeInMillis();
        long endTime = TimeHelper.getCurrentTimeInMillis() + TimeHelper.min2Millis(bookingPeriod);

        if (mCurrentEvent.isAvailable() && endTime <= mCurrentEvent.getNextBusyEventStartTime()) {
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

            event.setDescription(EventModel.FAST_BOOKING_DESCRIPTION);

            CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
            params.setEventParams(event);
            mInsertEventUseCase.init(params).execute(new InsertEventSubscriber());
        }
    }

    @Override
    public void deleteEvent() {
        if (mCurrentEvent.getId() != null) {
            mRoomEventsView.stopCountDown();
            Event event = new Event();
            event.setId(mCurrentEvent.getId());
            CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
            params.setEventParams(event);
            mDeleteEventUseCase.init(params).execute(new DeleteEventSubscriber());
        }
    }

    @Override
    public void updateEvent() {
        if (mEventList != null && mCurrentEventPos < mEventList.size() - 1 && mEventList.get(mCurrentEventPos + 1) != null) {
            EventModel eventModel = mEventList.get(mCurrentEventPos + 1);
            if (eventModel.isAvailable() && (eventModel.getNextBusyEventStartTime() - eventModel.getStartTime() >= TimeHelper.min2Millis(15))) {
                long extendedTime = TimeHelper.min2Millis(15);
                // Extent
                Event event = new Event();
                event.setId(mCurrentEvent.getId());

                DateTime endDateTime = new DateTime(mCurrentEvent.getEndTime() + extendedTime);
                EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/London");
                event.setEnd(end);

                CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
                params.setEventParams(event);
                mUpdateEventUseCase.init(params).execute(new UpdateEventSubscriber());
            }
        }
    }

    private final class GetEventsSubscriber extends DefaultSubscriber<List<EventModel>> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(mRoomEventsView.context().getString(R.string.loading) + "...");
        }

        @Override
        public void onNext(List<EventModel> roomEvents) {
            mEventList = roomEvents;
            mCurrentEventPos = getCurrentEventPosition(mEventList);
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
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                exception.printStackTrace();
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
    public void Resume() {
    }

    @Override
    public void Pause() {
    }

    @Override
    public void destroy() {
        mGetEventsUseCase.unSubscribe();
        mInsertEventUseCase.unSubscribe();
        mUpdateEventUseCase.unSubscribe();
        mDeleteEventUseCase.unSubscribe();
    }
}
