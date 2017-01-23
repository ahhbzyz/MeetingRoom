package com.badoo.meetingroom.presentation.presenter.impl;


import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.event.GetEvents;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomListView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListPresenterImpl implements RoomListPresenter {

    private RoomListView mRoomListView;
    private int mPage;
    private final GetEvents mGetEventsUseCase;
    private List<RoomModel> mRoomModelList;

    @Inject
    RoomListPresenterImpl(@Named(GetEvents.NAME) GetEvents getEventsUseCase) {
        mGetEventsUseCase = getEventsUseCase;
    }

    @Override
    public void setView(RoomListView view) {
        this.mRoomListView = view;
    }

    @Override
    public void getRoomEvents(List<RoomModel> roomModelList) {

        mRoomModelList = roomModelList;
        mRoomListView.renderRoomListInView(roomModelList);

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
            mRoomListView.showLoadingData("");
        }

        @Override
        public void onNext(List<EventModel> eventModelList) {
            if (eventModelList != null) {
                for (EventModel roomEventModel : eventModelList) {
                    if (roomEventModel.isExpired()) {
                        continue;
                    }
                    mRoomModelList.get(position).setCurrentEvent(roomEventModel);
                    break;
                }
            }
            mRoomListView.notifyItemChange(position);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mRoomListView.dismissLoadingData();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mRoomListView.dismissLoadingData();
            try {
                throw e;
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomListView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomListView.showError(exception.getMessage());
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
