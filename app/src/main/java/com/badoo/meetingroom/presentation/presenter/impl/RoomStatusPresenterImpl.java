package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomList;
import com.badoo.meetingroom.domain.interactor.GetRoomMap;
import com.badoo.meetingroom.domain.interactor.event.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.event.GetEvents;
import com.badoo.meetingroom.domain.interactor.event.InsertEvent;
import com.badoo.meetingroom.domain.interactor.event.UpdateEvent;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
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
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@PerActivity
public class RoomStatusPresenterImpl implements RoomStatusPresenter {

    private RoomStatusView mView;

    private final GetEvents mGetEventsUseCase;
    private final InsertEvent mInsertEventUseCase;
    private final DeleteEvent mDeleteEventUseCase;
    private final UpdateEvent mUpdateEventUseCase;
    private final GetRoomList mGetRoomListUseCase;

    private List<EventModel> mEventModelList;
    private List<RoomModel> mRoomModelList;

    private int mCurrentEventPos = -1;

    private EventModel mCurrentEvent;
    private HashSet<String> mConfirmedIds;

    @Inject
    RoomStatusPresenterImpl(GetEvents getEventsUseCase,
                            InsertEvent insertEventUseCase,
                            DeleteEvent deleteEventUseCase,
                            UpdateEvent updateEventUseCase,
                            GetRoomList getRoomListUseCase) {
        mGetEventsUseCase = getEventsUseCase;
        mInsertEventUseCase = insertEventUseCase;
        mDeleteEventUseCase = deleteEventUseCase;
        mUpdateEventUseCase = updateEventUseCase;
        mGetRoomListUseCase = getRoomListUseCase;
        mConfirmedIds = new HashSet<>();
        mEventModelList = new ArrayList<>();
    }

    @Override
    public void setView(@NonNull RoomStatusView roomEventsView) {
        mView = roomEventsView;
    }

    @Override
    public void onCountDownTicking() {
        mView.updateRoomStatusTextView(mCurrentEvent);
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
        mView.updateHorizontalTimeline();
    }

    @Override
    public void setDoNotDisturb(boolean isDoNotDisturb) {
        mCurrentEvent.setDoNotDisturb(isDoNotDisturb);
        if (mCurrentEvent.isDoNotDisturb()) {
            mView.hideTopBottomContent();
        }
        else {
            mView.showTopBottomContent();
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
            mView.bookRoomFrom(mCurrentEventPos, (ArrayList<EventModel>)mEventModelList);
        }
        else {
            mView.showEventOrganizerDialog(mCurrentEvent);
        }
    }

    @Override
    public void onRestart() {
        mCurrentEventPos = getCurrentEventPosition(mEventModelList);
        showCurrentEventOnCircleTimeView();
        onSystemTimeRefresh();
    }

    @Override
    public void onSystemTimeRefresh() {
        checkEventExtendable();
        updateNumOfAvailableRooms();
    }

    private void showCurrentEventOnCircleTimeView() {

        if (mEventModelList != null && mCurrentEventPos >= 0 && mCurrentEventPos < mEventModelList.size() && mEventModelList.get(mCurrentEventPos) != null) {

            mCurrentEvent = mEventModelList.get(mCurrentEventPos);

            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mCurrentEvent.setConfirmed(true);
            }

            mView.renderRoomEvent(mCurrentEvent);
            checkEventExtendable();
        }
    }

    private void moveToNextEvent() {
        if (mEventModelList != null && !mEventModelList.isEmpty()) {
            if (mConfirmedIds.contains(mCurrentEvent.getId())) {
                mConfirmedIds.remove(mCurrentEvent.getId());
            }
            mCurrentEventPos++;
        }
    }

    private void showEventsOnHorizontalTimelineView() {
        if (mEventModelList != null && !mEventModelList.isEmpty()) {
            mView.renderRoomEventList(mEventModelList);
        }
    }

    private void checkEventExtendable() {
        if (mCurrentEvent != null &&
            mCurrentEvent.isBusy() &&
            mCurrentEvent.isProcessing() &&
            mEventModelList != null &&
            mCurrentEventPos < mEventModelList.size() - 1
            && mEventModelList.get(mCurrentEventPos + 1) != null) {

            EventModel eventModel = mEventModelList.get(mCurrentEventPos + 1);
            if (eventModel.isAvailable() && (eventModel.getNextBusyEventStartTime() - eventModel.getStartTime() >= TimeHelper.min2Millis(15))) {
                mView.updateExtendButtonState(true);
                return;
            }
        }
        mView.updateExtendButtonState(false);
    }

    private int getCurrentEventPosition(List<EventModel> roomEventModelList) {
        for (EventModel eventModel : roomEventModelList) {
            if (eventModel.isProcessing()) {
                return roomEventModelList.indexOf(eventModel);
            }
        }
        return -1;
    }

    private void updateNumOfAvailableRooms() {

        if (mRoomModelList == null) {
            return;
        }

        int numOfAvailableRooms = 0;

        for (RoomModel roomModel : mRoomModelList) {
            if(roomModel.getCurrentEvent().isAvailable()) {
                numOfAvailableRooms ++;
            }
        }
        mView.updateNumOfAvailableRooms(numOfAvailableRooms);

    }

    @Override
    public void getNumOfAvailableRooms() {
       mGetRoomListUseCase.execute(new GetRoomListSubscriber(), true);
    }

    @Override
    public void getEvents() {

        if (Badoo.getCurrentRoom() != null) {
            mGetEventsUseCase.execute(new GetEventsSubscriber(), Badoo.getCurrentRoom().getId(), 0);
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
                .setDateTime(startDateTime);
            event.setStart(start);

            DateTime endDateTime = new DateTime(endTime);
            EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime);
            event.setEnd(end);

            event.setDescription(EventModel.FAST_BOOKING_DESCRIPTION);
            CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
            params.setEventParams(event);
            mInsertEventUseCase.execute(new InsertEventSubscriber(), params);
        }
    }

    @Override
    public void deleteEvent() {
        if (mCurrentEvent.getId() != null) {
            mView.stopCountDown();
            Event event = new Event();
            event.setId(mCurrentEvent.getId());
            CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
            params.setEventParams(event);
            mDeleteEventUseCase.execute(new DeleteEventSubscriber(), params);
        }
    }

    @Override
    public void updateEvent() {
        if (mEventModelList != null && mCurrentEventPos < mEventModelList.size() - 1 && mEventModelList.get(mCurrentEventPos + 1) != null) {
            EventModel eventModel = mEventModelList.get(mCurrentEventPos + 1);
            if (eventModel.isAvailable() && (eventModel.getNextBusyEventStartTime() - eventModel.getStartTime() >= TimeHelper.min2Millis(15))) {
                long extendedTime = TimeHelper.min2Millis(15);
                // Extent
                Event event = new Event();
                event.setId(mCurrentEvent.getId());

                DateTime endDateTime = new DateTime(mCurrentEvent.getEndTime() + extendedTime);
                EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime);
                event.setEnd(end);

                CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
                params.setEventParams(event);
                mUpdateEventUseCase.execute(new UpdateEventSubscriber(), params);
            }
        }
    }

    private final class GetEventsSubscriber extends DefaultSubscriber<List<EventModel>> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(mView.context().getString(R.string.loading) + "...");
        }

        @Override
        public void onNext(List<EventModel> roomEvents) {
            mEventModelList = roomEvents;
            mCurrentEventPos = getCurrentEventPosition(mEventModelList);
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
                mView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mView.showError(exception.getMessage());
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
            showViewLoading(mView.context().getString(R.string.booking) + "...");
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
                mView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mView.showError(exception.getMessage());
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
            showViewLoading(mView.context().getString(R.string.canceling) + "...");
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
                mView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mView.showError(exception.getMessage());
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
            showViewLoading(mView.context().getString(R.string.booking) + "...");
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
                mView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    private final class GetRoomListSubscriber extends DefaultSubscriber<List<RoomModel>> {
        @Override
        public void onStart() {
            super.onStart();
            mView.showLoadingData("");
        }

        @Override
        public void onNext(List<RoomModel> roomModelList) {
            super.onNext(roomModelList);
            mRoomModelList = roomModelList;
            updateNumOfAvailableRooms();
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mView.dismissLoadingData();
            try {
                throw e;
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void showViewLoading(String message) {
        mView.showLoadingData(message);
    }

    private void dismissViewLoading() {
        mView.dismissLoadingData();
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
