package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomMap;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomsPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomListView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/01/2017.
 */

public class RoomListPresenterImpl implements RoomsPresenter {

    private RoomListView mRoomListView;

    private final GetRoomMap mGetRoomMapUseCase;

    @Inject
    RoomListPresenterImpl(GetRoomMap getRoomMapUseCase) {
        mGetRoomMapUseCase = getRoomMapUseCase;
    }


    @Override
    public void getRoomList() {
        mGetRoomMapUseCase.execute(new GetRoomListSubscriber());
    }

    @Override
    public void setView(RoomListView roomListView) {
        mRoomListView = roomListView;
    }


    private final class GetRoomListSubscriber extends DefaultSubscriber<TreeMap<Integer, List<RoomModel>>> {
        @Override
        public void onStart() {
            super.onStart();
            mRoomListView.showLoadingData("");
        }

        @Override
        public void onNext(TreeMap<Integer, List<RoomModel>> roomModelListMap) {
            super.onNext(roomModelListMap);

            if (roomModelListMap == null) {
                return;
            }
            mRoomListView.setUpViewPager(roomModelListMap);
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
        mGetRoomMapUseCase.unSubscribe();
    }
}
