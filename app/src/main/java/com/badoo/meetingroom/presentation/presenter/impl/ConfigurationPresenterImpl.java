package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.exception.GooglePlayServicesAvailabilityException;
import com.badoo.meetingroom.data.exception.NoAccountNameFoundInCacheException;
import com.badoo.meetingroom.data.exception.NoPermissionToAccessContactsException;
import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.BindPushNotifications;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetRoomList;
import com.badoo.meetingroom.domain.interactor.GetRoomMap;
import com.badoo.meetingroom.domain.interactor.googleaccount.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.googleaccount.PutGoogleAccount;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.impl.GoogleAccountModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.ConfigurationPresenter;
import com.badoo.meetingroom.presentation.view.view.ConfigurationView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class ConfigurationPresenterImpl implements ConfigurationPresenter {

    private ConfigurationView mView;

    private final GetGoogleAccount mGetGoogleAccountUseCase;
    private final PutGoogleAccount mPutGoogleAccountUseCase;
    private final GetRoomList mGetRoomListUseCase;
    private final BindPushNotifications mBindPushNotificationsUseCase;

    @Inject
    ConfigurationPresenterImpl(GetGoogleAccount getGoogleAccountUseCase,
                               PutGoogleAccount putGoogleAccountUseCase,
                               GetRoomList getRoomListUseCase,
                               BindPushNotifications bindPushNotificationsUseCase) {

        mGetGoogleAccountUseCase = getGoogleAccountUseCase;
        mPutGoogleAccountUseCase = putGoogleAccountUseCase;
        mGetRoomListUseCase = getRoomListUseCase;
        mBindPushNotificationsUseCase = bindPushNotificationsUseCase;
    }

    @Override
    public void init() {
        loadGoogleAccount();
    }

    private void loadGoogleAccount() {
        mGetGoogleAccountUseCase.execute(new GetGoogleAccountSubscriber());
    }

    private void showViewLoading() {
        this.mView.showLoadingData("");
    }

    private void dismissViewLoading() {
        this.mView.dismissLoadingData();
    }

    private void showAccountNameOnSnackBar(GoogleAccountModel googleAccountModel) {
        mView.showAccountNameOnSnackBar(googleAccountModel.getAccountName());
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int code){
        mView.showGooglePlayServicesAvailabilityErrorDialog(code);
    }

    private void showRequestPermissionsDialog() {
        mView.showRequestPermissionsDialog();
    }

    private void showChooseAccountDialog() {
        mView.showChooseAccountDialog();
    }

    @Override
    public void setView(@NonNull ConfigurationView configurationView) {
        this.mView = configurationView;
    }

    @Override
    public void onNoGooglePlayServicesError() {
        mView.showNoGooglePlayServicesOnSnackBar();
    }

    @Override
    public void storeGoogleAccountName(String accountName) {
        mPutGoogleAccountUseCase.execute(new PutGoogleAccountSubscriber(), accountName);
    }

    @Override
    public void bindPushNotificationsWithRoom(String id) {
        CalendarApiParams params = new CalendarApiParams(id);
        params.setRoomName(Badoo.getCurrentRoom().getName());
        mBindPushNotificationsUseCase.execute(new BindPushNotificationsSubscriber(), params);
    }

    @Override
    public void finishConfigurations() {
        mView.showRoomStatusView();
        //bindPushNotificationsWithRoom(Badoo.getCurrentRoom().getId());
        //FirebaseMessaging.getInstance().subscribeToTopic(Badoo.getCurrentRoom().getName());
    }

    private final class GetGoogleAccountSubscriber extends DefaultSubscriber<GoogleAccountModel> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading();
        }

        @Override
        public void onNext(GoogleAccountModel googleAccountModel) {
            super.onNext(googleAccountModel);
            showAccountNameOnSnackBar(googleAccountModel);
            mGetRoomListUseCase.execute(new GetRoomListSubscriber(), false);
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
                mView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mView.showError(exception.getMessage());
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

    private final class GetRoomListSubscriber extends DefaultSubscriber<List<RoomModel>> {
        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onNext(List<RoomModel> roomModelList) {
            super.onNext(roomModelList);
            mView.setUpRoomListSpinner(roomModelList);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
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

    private final class BindPushNotificationsSubscriber extends DefaultSubscriber<Channel> {

        @Override
        public void onStart() {
            super.onStart();
            mView.showLoadingData("Binding push notifications");
        }

        @Override
        public void onNext(Channel channel) {
            super.onNext(channel);
            if (channel != null) {
                mView.showRoomStatusView();
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mView.dismissLoadingData();
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
        mGetGoogleAccountUseCase.unSubscribe();
        mPutGoogleAccountUseCase.unSubscribe();
        mGetRoomListUseCase.unSubscribe();
        mBindPushNotificationsUseCase.unSubscribe();
    }
}
