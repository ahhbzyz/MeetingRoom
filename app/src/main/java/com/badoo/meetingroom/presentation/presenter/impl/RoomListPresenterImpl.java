package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetCalendarList;
import com.badoo.meetingroom.domain.interactor.event.GetEvents;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.mapper.RoomModelMapper;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.model.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomListView;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListPresenterImpl implements RoomListPresenter {

    private RoomListView mRoomListView;
    private final GetCalendarList mGetCalendarListUseCase;
    private final GetEvents mGetEventsUseCase;
    private final RoomModelMapper mRoomModelMapper;
    private final RoomEventModelMapper mRoomEventModelMapper;
    private List<RoomModel> mRoomModelList;
    private int mPage;

    @Inject
    RoomListPresenterImpl(@Named(GetCalendarList.NAME) GetCalendarList getCalendarListUseCase,
                          @Named(GetEvents.NAME) GetEvents getEventsUseCase,
                          RoomModelMapper roomModelMapper, RoomEventModelMapper roomEventModelMapper) {

        mGetCalendarListUseCase = getCalendarListUseCase;
        mGetEventsUseCase = getEventsUseCase;
        mRoomModelMapper = roomModelMapper;
        mRoomEventModelMapper = roomEventModelMapper;
        mRoomModelList = new ArrayList<>();
    }

    @Override
    public void setView(RoomListView view) {
        this.mRoomListView = view;
    }

    @Override
    public void getRoomList() {
        mPage = mRoomListView.getPage();
        mGetCalendarListUseCase.execute(new GetCalendarListSubscriber());
    }


    private final class GetCalendarListSubscriber extends DefaultSubscriber<List<Room>> {
        @Override
        public void onStart() {
            super.onStart();
            mRoomListView.showLoadingData("");
        }

        @Override
        public void onNext(List<Room> roomList) {
            super.onNext(roomList);

            if (roomList == null) {
                return;
            }

            List<RoomModel> temp = mRoomModelMapper.map(roomList);
            mRoomModelList.clear();
            for (RoomModel roomModel: temp) {
                if (mPage == 0 && roomModel.getName().toLowerCase().contains("groundfloor")) {
                    mRoomModelList.add(roomModel);
                }
                if (mPage == 1 && roomModel.getName().toLowerCase().contains("4th")) {
                    mRoomModelList.add(roomModel);
                }
            }

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

            mRoomEventModelMapper.setEventStartTime(startDateTime.getValue());
            mRoomEventModelMapper.setEventEndTime(endDateTime.getValue());

            for (int i = 0; i < mRoomModelList.size(); i++) {
                CalendarApiParams params = new CalendarApiParams(mRoomModelList.get(i).getId());
                params.setEventParams(event);
                GetEventsSubscriber subscriber = new GetEventsSubscriber();
                subscriber.setPosition(i);
                mGetEventsUseCase.init(params).execute(subscriber);
            }
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
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                //mRoomListView.handleRecoverableAuthException(userRecoverableAuthIOException);
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

    private final class GetEventsSubscriber extends DefaultSubscriber<List<RoomEvent>> {

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
        public void onNext(List<RoomEvent> roomEvents) {
            List<RoomEventModel> roomEventModelList = mRoomEventModelMapper.map(roomEvents);
            if (roomEventModelList != null && !roomEventModelList.isEmpty()) {
                for (RoomEventModel roomEventModel : roomEventModelList) {
                    if (roomEventModel.isExpired()) {
                        continue;
                    }
                    mRoomModelList.get(position).setCurrentEvent(roomEventModel);
                    break;
                }
            }
            mRoomListView.renderRoomListInView(mRoomModelList);
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
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                //mRoomListView.handleRecoverableAuthException(userRecoverableAuthIOException);
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
        mGetCalendarListUseCase.unSubscribe();
    }
}
