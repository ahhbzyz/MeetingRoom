package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.exception.GooglePlayServicesAvailabilityException;
import com.badoo.meetingroom.data.exception.NoAccountNameFoundInCacheException;
import com.badoo.meetingroom.data.exception.NoPermissionToAccessContactsException;
import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetCalendarList;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.PutGoogleAccount;
import com.badoo.meetingroom.presentation.mapper.GoogleAccountModelMapper;
import com.badoo.meetingroom.presentation.mapper.RoomModelMapper;
import com.badoo.meetingroom.presentation.model.GoogleAccountModel;
import com.badoo.meetingroom.presentation.model.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.MainPresenter;
import com.badoo.meetingroom.presentation.view.view.MainView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;

    private final GetGoogleAccount mGetGoogleAccountUseCase;
    private final PutGoogleAccount mPutGoogleAccountUseCase;
    private final GetCalendarList mGetCalendarListUseCase;
    private final GoogleAccountModelMapper mGoogleAccountMapper;
    private final RoomModelMapper mRoomModelMapper;

    @Inject
    MainPresenterImpl(@Named(GetGoogleAccount.NAME) GetGoogleAccount getGoogleAccountUseCase,
                      @Named(PutGoogleAccount.NAME) PutGoogleAccount putGoogleAccountUseCase,
                      GetCalendarList getCalendarListUseCase,
                      GoogleAccountModelMapper googleAccountModelMapper,
                      RoomModelMapper roomModelMapper) {
        this.mGetGoogleAccountUseCase = getGoogleAccountUseCase;
        this.mPutGoogleAccountUseCase = putGoogleAccountUseCase;
        this.mGetCalendarListUseCase = getCalendarListUseCase;
        this.mGoogleAccountMapper = googleAccountModelMapper;
        this.mRoomModelMapper = roomModelMapper;
    }

    @Override
    public void init() {
        loadGoogleAccount();
    }

    private void loadGoogleAccount() {
        mGetGoogleAccountUseCase.execute(new GetGoogleAccountSubscriber());
    }

    private void showViewLoading() {
        this.mMainView.showLoadingData("");
    }

    private void dismissViewLoading() {
        this.mMainView.dismissLoadingData();
    }

    private void showAccountNameOnSnackBar(GoogleAccount account) {
        GoogleAccountModel googleAccountModel = this.mGoogleAccountMapper.map(account);
        mMainView.showAccountNameOnSnackBar(googleAccountModel.getAccountName());
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int code){
        mMainView.showGooglePlayServicesAvailabilityErrorDialog(code);
    }

    private void showRequestPermissionsDialog() {
        mMainView.showRequestPermissionsDialog();
    }

    private void showChooseAccountDialog() {
        mMainView.showChooseAccountDialog();
    }

    @Override
    public void setView(@NonNull MainView mainView) {
        this.mMainView = mainView;
    }

    @Override
    public void onNoGooglePlayServicesError() {
        mMainView.showNoGooglePlayServicesOnSnackBar();
    }

    @Override
    public void storeGoogleAccountName(String accountName) {
        mPutGoogleAccountUseCase.init(accountName).execute(new PutGoogleAccountSubscriber());
    }

    private final class GetGoogleAccountSubscriber extends DefaultSubscriber<GoogleAccount> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading();
        }

        @Override
        public void onNext(GoogleAccount googleAccount) {
            super.onNext(googleAccount);
            showAccountNameOnSnackBar(googleAccount);
            mGetCalendarListUseCase.execute(new GetCalendarListSubscriber());
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            dismissViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            try {
                dismissViewLoading();
                throw e;

            } catch (GooglePlayServicesAvailabilityException e1) {

                showGooglePlayServicesAvailabilityErrorDialog(e1.getConnectionStatusCode());

            } catch (NoPermissionToAccessContactsException e2) {

                showRequestPermissionsDialog();

            } catch (NoAccountNameFoundInCacheException e3) {

                showChooseAccountDialog();

            } catch (GoogleJsonResponseException googleJsonResponseException) {

                mMainView.showError(googleJsonResponseException.getDetails().getMessage());

            } catch (Exception exception) {

                mMainView.showError(exception.getMessage());

            } catch (Throwable throwable) {

                throwable.printStackTrace();

            }
        }
    }

    private final class PutGoogleAccountSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading();
        }

        @Override
        public void onNext(Void aVoid) {
            super.onNext(aVoid);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            dismissViewLoading();
            loadGoogleAccount();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }
    }

    private final class GetCalendarListSubscriber extends DefaultSubscriber<List<Room>> {
        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onNext(List<Room> roomList) {
            super.onNext(roomList);
            List<RoomModel> roomModelList = mRoomModelMapper.map(roomList);
            mMainView.setUpRoomListSpinner(roomModelList);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
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
        mGetGoogleAccountUseCase.unSubscribe();
        mPutGoogleAccountUseCase.unSubscribe();
        mGetCalendarListUseCase.unSubscribe();
    }
}
