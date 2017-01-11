package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.InsertEvent;
import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class RoomBookingPresenterImpl implements RoomBookingPresenter {

    private RoomBookingView mRoomBookingView;
    private List<TimeSlotsAdapter.TimeSlot> mTimeSlotList;
    private long selectedStartTime = -1;
    private long selectedEndTime = -1;
    private final InsertEvent mInsertEventUseCase;

    @Inject
    RoomBookingPresenterImpl(@Named(InsertEvent.NAME) InsertEvent insertEvent) {
        this.mInsertEventUseCase = insertEvent;
    }


    public void init() {
        setTimeSlotsInView();
    }

    private void setTimeSlotsInView() {
        mRoomBookingView.setTimeSlotsInView();
    }

    @Override
    public void setView(RoomBookingView roomBookingView) {
        this.mRoomBookingView = roomBookingView;
    }

    @Override
    public void bookRoom(String organizer) {
        Event event = new Event();
        DateTime startDateTime = new DateTime(selectedStartTime);
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        event.setStart(start);

        DateTime endDateTime = new DateTime(selectedEndTime);
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);

        List<EventAttendee> eventAttendees = new ArrayList<>(1);
        eventAttendees.add(new EventAttendee().setEmail(organizer));
        event.setAttendees(eventAttendees);

        this.mInsertEventUseCase.init(event).execute(new InsertEventSubscriber());
    }

    @Override
    public void setTimeSlotList(List<TimeSlotsAdapter.TimeSlot> timeSlotList) {
        this.mTimeSlotList = timeSlotList;
        boolean startTimeHasVal = false;
        boolean isFirstSelectedSlot = false;
        int numOfSelectedSlots = 0;

        for (int i = 0; i < timeSlotList.size(); i++) {

            TimeSlotsAdapter.TimeSlot slot = timeSlotList.get(i);

            if (!slot.isSelected() && !isFirstSelectedSlot) {
                continue;
            }
            isFirstSelectedSlot = true;
            if (!startTimeHasVal) {
                selectedStartTime = slot.getStartTime();
                startTimeHasVal = true;
            }
            numOfSelectedSlots++;
            if (!slot.isSelected() || i == timeSlotList.size() - 1) {
                selectedEndTime = slot.getStartTime();
                break;
            }
        }

        if (numOfSelectedSlots == 0) {
            selectedStartTime = -1;
            selectedEndTime = -1;
        }
        mRoomBookingView.updateTimePeriodTextView(selectedStartTime, selectedEndTime);
    }

    private final class InsertEventSubscriber extends DefaultSubscriber<Event> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading();
        }

        @Override
        public void onNext(Event event) {
            super.onNext(event);
            if (event.getStatus().equals("confirmed")){
               mRoomBookingView.showBookingSuccessful();
            }
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
            } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomBookingView.showRecoverableAuth(userRecoverableAuthIOException);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomBookingView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomBookingView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    private void showViewLoading() {
        this.mRoomBookingView.showLoadingData("Booking...");
    }

    private void dismissViewLoading() {this.mRoomBookingView.dismissLoadingData();}


    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {
        mInsertEventUseCase.unSubscribe();
    }
}
