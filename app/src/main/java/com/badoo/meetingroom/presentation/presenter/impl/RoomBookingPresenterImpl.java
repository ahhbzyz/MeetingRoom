package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.data.InsertEventParams;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.InsertEvent;
import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonParser;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import org.json.JSONArray;

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
    private final GoogleAccountCredential mCredential;

    @Inject
    RoomBookingPresenterImpl(@Named(InsertEvent.NAME) InsertEvent insertEvent, GoogleAccountCredential credential) {
        this.mInsertEventUseCase = insertEvent;
        this.mCredential = credential;
    }

    @Override
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
        DateTime startDateTime = new DateTime(selectedStartTime);
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        DateTime endDateTime = new DateTime(selectedEndTime);
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        InsertEventParams params = new InsertEventParams.EventParamsBuilder(mCredential)
            .startDateTime(start)
            .endDateTime(end)
            .organizer(organizer)
            .build();
        this.mInsertEventUseCase.init(params).execute(new InsertEventSubscriber());
    }

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
            showViewLoading(true);
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
            showViewLoading(false);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            showViewLoading(false);
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


    private void showViewLoading(boolean visibility) {
        this.mRoomBookingView.showLoadingData(visibility);
    }


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
