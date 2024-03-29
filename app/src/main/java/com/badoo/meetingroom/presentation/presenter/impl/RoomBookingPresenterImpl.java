package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetPersons;
import com.badoo.meetingroom.domain.interactor.event.InsertEvent;
import com.badoo.meetingroom.presentation.model.impl.EventModelImpl;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class RoomBookingPresenterImpl implements RoomBookingPresenter {

    private RoomBookingView mRoomBookingView;
    private long mSelectedStartTime;
    private long mSelectedEndTime;
    private final InsertEvent mInsertEventUseCase;
    private final GetPersons mGetPersonsUseCase;
    private List<EventModel> mAvailableEventList;

    @Inject
    RoomBookingPresenterImpl(InsertEvent insertEvent,
                             GetPersons getPersons) {
        mInsertEventUseCase = insertEvent;
        mGetPersonsUseCase = getPersons;
        mAvailableEventList = new ArrayList<>();
    }

    @Override
    public void getContactList() {
        mGetPersonsUseCase.execute(new GetPersonsSubscriber());
    }

    @Override
    public void setView(RoomBookingView roomBookingView) {
        this.mRoomBookingView = roomBookingView;
    }

    @Override
    public void bookRoom(String organizer, String roomId) {
        Event event = new Event();
        DateTime startDateTime = new DateTime(mSelectedStartTime);
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(mSelectedEndTime);
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime);
        event.setEnd(end);

        event.setSummary(mRoomBookingView.context().getString(R.string.mobile_booking));

        List<EventAttendee> eventAttendees = new ArrayList<>(1);
        eventAttendees.add(new EventAttendee().setEmail(organizer));
        event.setAttendees(eventAttendees);

        CalendarApiParams params = new CalendarApiParams(roomId);
        params.setEventParams(event);
        mInsertEventUseCase.execute(new InsertEventSubscriber(), params);
    }

    @Override
    public void setTimeSlotList(int position, List<EventModel> eventModelList) {

        mAvailableEventList.clear();

        int tempPos = position;
        while (tempPos >= 0 && !eventModelList.get(tempPos).isProcessing()) {
            tempPos --;
        }

        if (tempPos == -1) {
            tempPos++;
        }

        while(tempPos < eventModelList.size() - 1 && eventModelList.get(tempPos).isBusy()) {
            tempPos++;
        }

        for (int i = tempPos; i < eventModelList.size(); i++) {
            mAvailableEventList.add(eventModelList.get(i));
        }

        if (!eventModelList.isEmpty()) {
            EventModel eventModel = new EventModelImpl();
            eventModel.setStartTime(TimeHelper.getCurrentTimeInMillis());
            eventModel.setEndTime(mAvailableEventList.get(0).getStartTime());
            eventModel.setStatus(EventModel.AVAILABLE);

            //mAvailableEventList.add(0, eventModel);
        }

        if (mAvailableEventList == null && mAvailableEventList.isEmpty()) {
            return;
        }

        String date;

        if (mAvailableEventList.get(0).getStartTime() >= TimeHelper.getMidNightTimeOfDay(1)) {

            date = TimeHelper.formatDate(mAvailableEventList.get(0).getStartTime());
        } else {
            date = mRoomBookingView.context().getString(R.string.today);
        }

        mRoomBookingView.renderTimeSlotsInView(position - tempPos, mAvailableEventList, date);
    }

    @Override
    public void updateSelectedTimePeriod(int startIndex, int endIndex) {
        if (startIndex >= 0 && endIndex < mAvailableEventList.size()) {
            mSelectedStartTime = mAvailableEventList.get(startIndex).getStartTime();
            mSelectedEndTime = mAvailableEventList.get(endIndex).getEndTime();
        } else {
            mSelectedStartTime = -1;
            mSelectedEndTime = -1;
        }
        mRoomBookingView.updateTimePeriodTextView(mSelectedStartTime, mSelectedEndTime);
    }


    private final class InsertEventSubscriber extends DefaultSubscriber<Event> {

        @Override
        public void onStart() {
            super.onStart();
            mRoomBookingView.showLoadingData(mRoomBookingView.context().getString(R.string.booking) + "...");
        }

        @Override
        public void onNext(Event event) {
            super.onNext(event);
            if (event.getStatus().equals("confirmed")){
                if (event.getStart().getDateTime() != null) {
                    mRoomBookingView.showBookingSuccessful(event.getStart().getDateTime().getValue());
                }
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
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomBookingView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomBookingView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    private final class GetPersonsSubscriber extends DefaultSubscriber<List<PersonModel>> {
        @Override
        public void onStart() {
            super.onStart();
            mRoomBookingView.showLoadingData(mRoomBookingView.context().getString(R.string.loading) + "...");
        }

        @Override
        public void onNext(List<PersonModel> localPersonList) {
            super.onNext(localPersonList);
            mRoomBookingView.setUpAutoCompleteTextView(localPersonList);
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
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomBookingView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mRoomBookingView.showError(exception.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void dismissViewLoading() {
        mRoomBookingView.dismissLoadingData();
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
        mGetPersonsUseCase.unSubscribe();
    }
}
