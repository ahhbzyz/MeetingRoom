package com.badoo.meetingroom.presentation.presenter;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.exception.GooglePlayServicesAvailabilityException;
import com.badoo.meetingroom.data.exception.NoAccountNameFoundInCacheException;
import com.badoo.meetingroom.data.exception.NoPermissionToAccessContacts;
import com.badoo.meetingroom.domain.entity.GoogleAccount;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.WriteGoogleAccount;
import com.badoo.meetingroom.presentation.mapper.GoogleAccountModelMapper;
import com.badoo.meetingroom.presentation.model.GoogleAccountModel;
import com.badoo.meetingroom.presentation.view.GetCredentialView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GetCredentialPresenterImpl implements GetCredentialPresenter, OnGetCredentialListner {

    private GetCredentialView mGetCredentialView;

    private GetGoogleAccount mGetGoogleAccountUseCase;
    private WriteGoogleAccount mWriteGoogleAccountUseCase;

    private final GoogleAccountModelMapper mMapper;

    @Inject
    public GetCredentialPresenterImpl(@Named(GetGoogleAccount.NAME) GetGoogleAccount getGoogleAccountUseCase,
                                      @Named(WriteGoogleAccount.NAME) WriteGoogleAccount writeGoogleAccountUseCase,
                                      GoogleAccountModelMapper mapper) {
        this.mGetGoogleAccountUseCase = getGoogleAccountUseCase;
        this.mWriteGoogleAccountUseCase = writeGoogleAccountUseCase;
        this.mMapper = mapper;
    }

    @Override
    public void init() {
        loadGoogleAccount();
    }

    private void loadGoogleAccount() {
        showViewLoading(true);
        mGetGoogleAccountUseCase.execute(new GetGoogleAccountSubscriber());
    }

    private void showViewLoading(boolean visibility) {
        this.mGetCredentialView.showLoadingData(visibility);
    }

    private void showAccountNameOnSnackBar(GoogleAccount account) {
        GoogleAccountModel googleAccountModel = this.mMapper.map(account);
        mGetCredentialView.showAccountNameOnSnackBar(googleAccountModel.getAccountName());
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int code){
        mGetCredentialView.showGooglePlayServicesAvailabilityErrorDialog(code);
    }

    private void requestPermissionToAccessContacts() {
        mGetCredentialView.showRequestPermissionsDialog();
    }

    private void chooseNewGoogleAccount() {
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

    }

    @Override
    public void onNoGooglePlayServicesError() {
        mGetCredentialView.showNoGooglePlayServicesOnSnackBar();
    }

    @Override
    public void storeGoogleAccountName(String accountName) {
        mWriteGoogleAccountUseCase.init(accountName).execute(new WriteGoogleAccountSubscriber());
    }

    private final class GetGoogleAccountSubscriber extends DefaultSubscriber<GoogleAccount> {
        @Override
        public void onNext(GoogleAccount googleAccount) {
            super.onNext(googleAccount);
            showAccountNameOnSnackBar(googleAccount);
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

            } catch (GooglePlayServicesAvailabilityException e1) {

                showGooglePlayServicesAvailabilityErrorDialog(e1.getConnectionStatusCode());

            } catch (NoPermissionToAccessContacts e2) {

                requestPermissionToAccessContacts();

            } catch (NoAccountNameFoundInCacheException e3) {

                chooseNewGoogleAccount();

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }

    private final class WriteGoogleAccountSubscriber extends DefaultSubscriber<Void> {

        @Override
        public void onNext(Void aVoid) {
            super.onNext(aVoid);
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
}
