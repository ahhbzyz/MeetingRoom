package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomList;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.EventsCalendarPresenter;
import com.badoo.meetingroom.presentation.view.view.EventsCalendarView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public class EventsCalendarPresenterImpl implements EventsCalendarPresenter {

    private EventsCalendarView mView;
    private final GetRoomList mGetRoomListUseCase;

    private List<RoomModel> mRoomModelList;

    @Inject
    EventsCalendarPresenterImpl(GetRoomList getRoomListUseCase) {

        mGetRoomListUseCase = getRoomListUseCase;
    }


    @Override
    public void setView(@NonNull EventsCalendarView eventsCalendarView) {
        mView = eventsCalendarView;
    }

    @Override
    public void getNumOfAvailableRooms() {
        mGetRoomListUseCase.execute(new GetRoomListSubscriber(), true);
    }

    @Override
    public void updateNumOfAvailableRooms() {
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


    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {

    }
}
