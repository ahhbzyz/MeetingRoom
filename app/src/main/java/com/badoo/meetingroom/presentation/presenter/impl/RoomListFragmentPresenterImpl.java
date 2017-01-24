package com.badoo.meetingroom.presentation.presenter.impl;


import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.event.GetEvents;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomListFragmentView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;


/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListFragmentPresenterImpl implements RoomListPresenter {

    private RoomListFragmentView mRoomListFragmentView;
    private int mPage;
    private final GetEvents mGetEventsUseCase;
    private List<RoomModel> mRoomModelList;
    private int useCaseFinishCount;

    @Inject
    RoomListFragmentPresenterImpl(@Named(GetEvents.NAME) GetEvents getEventsUseCase) {
        mGetEventsUseCase = getEventsUseCase;
    }

    @Override
    public void setView(RoomListFragmentView view) {
        this.mRoomListFragmentView = view;
    }

    @Override
    public void getRoomEvents(List<RoomModel> roomModelList) {

        mRoomModelList = roomModelList;

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

        useCaseFinishCount = 0;

        for (int i = 0; i < roomModelList.size(); i++) {
            CalendarApiParams params = new CalendarApiParams(roomModelList.get(i).getId());
            params.setEventParams(event);
            GetEventsSubscriber subscriber = new GetEventsSubscriber();
            subscriber.setPosition(i);
            mGetEventsUseCase.init(params, TimeHelper.getMidNightTimeOfDay(0), TimeHelper.getMidNightTimeOfDay(1)).execute(subscriber);
        }
    }

    @Override
    public void setPage(int page) {
        mPage = page;
    }

    private final class GetEventsSubscriber extends DefaultSubscriber<List<EventModel>> {

        private int position;

        private void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onStart() {
            super.onStart();
            mRoomListFragmentView.showLoadingData("");
        }

        @Override
        public void onNext(List<EventModel> eventModelList) {
            if (eventModelList != null) {
                mRoomModelList.get(position).setEventModelList(eventModelList);
            }
            useCaseFinishCount ++;

            if (useCaseFinishCount == mRoomModelList.size()) {

                Observable.from(mRoomModelList).toSortedList((o1, o2) -> {

                    if (o1.getCurrentEvent().isAvailable() && o2.getCurrentEvent().isBusy()) {
                        return -1;
                    } else if (o1.getCurrentEvent().isBusy() && o2.getCurrentEvent().isAvailable()) {
                        return 1;
                    } else {
                        return (int) (o2.getCurrentEvent().getNextBusyEventStartTime() - o1.getCurrentEvent().getNextBusyEventStartTime());
                    }
                }).subscribe(roomModelList -> {
                    mRoomModelList = roomModelList;
                    mRoomListFragmentView.renderRoomListInView(mRoomModelList);
                });
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mRoomListFragmentView.dismissLoadingData();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mRoomListFragmentView.dismissLoadingData();
            try {
                throw e;
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomListFragmentView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomListFragmentView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
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
