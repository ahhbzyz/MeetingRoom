package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.exception.GooglePlayServicesAvailabilityException;
import com.badoo.meetingroom.data.exception.NoAccountNameFoundInCacheException;
import com.badoo.meetingroom.data.exception.NoPermissionToAccessContactsException;
import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.domain.interactor.PutGoogleAccount;
import com.badoo.meetingroom.presentation.mapper.GoogleAccountModelMapper;
import com.badoo.meetingroom.presentation.mapper.RoomEventModelMapper;
import com.badoo.meetingroom.presentation.model.GoogleAccountModel;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.intf.GetCredentialPresenter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.GetCredentialView;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GetCredentialPresenterImpl implements GetCredentialPresenter {

    private GetCredentialView mGetCredentialView;

    private final GetGoogleAccount mGetGoogleAccountUseCase;
    private final PutGoogleAccount mPutGoogleAccountUseCase;
    private final GetRoomEventList mGetRoomEventListUseCase;
    private final GoogleAccountModelMapper mGoogleAccountMapper;
    private final RoomEventModelMapper mRoomEventModelMapper;

    @Inject
    GetCredentialPresenterImpl(@Named(GetGoogleAccount.NAME) GetGoogleAccount getGoogleAccountUseCase,
                               @Named(PutGoogleAccount.NAME) PutGoogleAccount putGoogleAccountUseCase,
                               @Named(GetRoomEventList.NAME) GetRoomEventList getRoomEventListUseCase,
                               GoogleAccountModelMapper googleAccountModelMapper,
                               RoomEventModelMapper roomEventModelMapper) {
        this.mGetGoogleAccountUseCase = getGoogleAccountUseCase;
        this.mPutGoogleAccountUseCase = putGoogleAccountUseCase;
        this.mGetRoomEventListUseCase = getRoomEventListUseCase;
        this.mGoogleAccountMapper = googleAccountModelMapper;
        this.mRoomEventModelMapper = roomEventModelMapper;
    }

    @Override
    public void init() {
        showViewLoading(true);
        loadGoogleAccount();
    }

    private void getModifyCalendarAuth() {

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

        this.mGetRoomEventListUseCase.init(event).execute(new RoomEventListSubscriber());
    }

    private void loadGoogleAccount() {
        mGetGoogleAccountUseCase.execute(new GetGoogleAccountSubscriber());
    }

    private void showViewLoading(boolean visibility) {
        this.mGetCredentialView.showLoadingData(visibility);
    }

    private void showAccountNameOnSnackBar(GoogleAccount account) {
        GoogleAccountModel googleAccountModel = this.mGoogleAccountMapper.map(account);
        mGetCredentialView.showAccountNameOnSnackBar(googleAccountModel.getAccountName());
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int code){
        mGetCredentialView.showGooglePlayServicesAvailabilityErrorDialog(code);
    }

    private void showRequestPermissionsDialog() {
        mGetCredentialView.showRequestPermissionsDialog();
    }

    private void showChooseAccountDialog() {
        mGetCredentialView.showChooseAccountDialog();
    }


    @Override
    public void setView(@NonNull GetCredentialView getCredentialView) {
        this.mGetCredentialView = getCredentialView;
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
        mGetRoomEventListUseCase.unSubscribe();
    }

    @Override
    public void onNoGooglePlayServicesError() {
        mGetCredentialView.showNoGooglePlayServicesOnSnackBar();
    }

    @Override
    public void storeGoogleAccountName(String accountName) {
        mPutGoogleAccountUseCase.init(accountName).execute(new PutGoogleAccountSubscriber());
    }

    private final class GetGoogleAccountSubscriber extends DefaultSubscriber<GoogleAccount> {
        @Override
        public void onNext(GoogleAccount googleAccount) {
            super.onNext(googleAccount);
            if (googleAccount.getAccountName() == null) {
                //TODO
            }
            showAccountNameOnSnackBar(googleAccount);
            getModifyCalendarAuth();
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            showViewLoading(false);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            try {
                showViewLoading(false);
                throw e;

            } catch (GooglePlayServicesAvailabilityException e1) {

                showGooglePlayServicesAvailabilityErrorDialog(e1.getConnectionStatusCode());

            } catch (NoPermissionToAccessContactsException e2) {

                showRequestPermissionsDialog();

            } catch (NoAccountNameFoundInCacheException e3) {

                showChooseAccountDialog();

            } catch (GoogleJsonResponseException googleJsonResponseException) {

                mGetCredentialView.showError(googleJsonResponseException.getDetails().getMessage());

            } catch (Exception exception) {

                mGetCredentialView.showError(exception.getMessage());

            } catch (Throwable throwable) {

                throwable.printStackTrace();

            }
        }
    }

    private final class PutGoogleAccountSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onStart() {
            super.onStart();
            showViewLoading(true);
        }

        @Override
        public void onNext(Void aVoid) {
            super.onNext(aVoid);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            showViewLoading(false);
            loadGoogleAccount();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }
    }

    private final class RoomEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {

        @Override
        public void onNext(List<RoomEvent> roomEvents) {
            Collection<RoomEventModel> mEventModelList = mRoomEventModelMapper.map(roomEvents);
            if (mEventModelList != null) {
                mGetCredentialView.showConnectGoogleCalendarSuccessful();
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
            } catch (UserRecoverableAuthIOException e1) {
                mGetCredentialView.showRecoverableAuth(e1);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }
}
