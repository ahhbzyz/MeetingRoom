package com.badoo.meetingroom.presentation.presenter.impl;

import android.util.SparseArray;

import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomList;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomsPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomsView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 22/01/2017.
 */

public class RoomsPresenterImpl implements RoomsPresenter {

    private RoomsView mRoomsView;

    private final GetRoomList mGetRoomListUseCase;

    @Inject
    RoomsPresenterImpl(@Named(GetRoomList.NAME) GetRoomList getRoomListUseCase) {
        mGetRoomListUseCase = getRoomListUseCase;
    }


    @Override
    public void getRoomList() {
        mGetRoomListUseCase.execute(new GetCalendarListSubscriber());
    }

    @Override
    public void setView(RoomsView roomsView) {
        mRoomsView = roomsView;
    }


    private final class GetCalendarListSubscriber extends DefaultSubscriber<SparseArray<List<RoomModel>>> {
        @Override
        public void onStart() {
            super.onStart();
            mRoomsView.showLoadingData("");
        }

        @Override
        public void onNext(SparseArray<List<RoomModel>> roomModelListMap) {
            super.onNext(roomModelListMap);

            if (roomModelListMap == null) {
                return;
            }

            mRoomsView.setUpViewPager(roomModelListMap);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mRoomsView.dismissLoadingData();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mRoomsView.dismissLoadingData();
            try {
                throw e;
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomsView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomsView.showError(exception.getMessage());
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
        mGetRoomListUseCase.unSubscribe();
    }
}
