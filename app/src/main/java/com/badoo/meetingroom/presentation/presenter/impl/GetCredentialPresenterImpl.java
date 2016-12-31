package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.exception.GooglePlayServicesAvailabilityException;
import com.badoo.meetingroom.data.exception.NoAccountNameFoundInCacheException;
import com.badoo.meetingroom.data.exception.NoPermissionToAccessContactsException;
import com.badoo.meetingroom.domain.entity.GoogleAccount;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.PutGoogleAccount;
import com.badoo.meetingroom.presentation.mapper.GoogleAccountModelMapper;
import com.badoo.meetingroom.presentation.model.GoogleAccountModel;
import com.badoo.meetingroom.presentation.presenter.intf.GetCredentialPresenter;
import com.badoo.meetingroom.presentation.view.view.GetCredentialView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GetCredentialPresenterImpl implements GetCredentialPresenter {

    private GetCredentialView mGetCredentialView;

    private GetGoogleAccount mGetGoogleAccountUseCase;
    private PutGoogleAccount mPutGoogleAccountUseCase;

    private final GoogleAccountModelMapper mMapper;

    @Inject
    GetCredentialPresenterImpl(@Named(GetGoogleAccount.NAME) GetGoogleAccount getGoogleAccountUseCase,
                                      @Named(PutGoogleAccount.NAME) PutGoogleAccount putGoogleAccountUseCase,
                                      GoogleAccountModelMapper mapper) {
        this.mGetGoogleAccountUseCase = getGoogleAccountUseCase;
        this.mPutGoogleAccountUseCase = putGoogleAccountUseCase;
        this.mMapper = mapper;
    }

    @Override
    public void init() {
        showViewLoading(true);
        loadGoogleAccount();
    }

    private void loadGoogleAccount() {
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

            } catch (Throwable throwable) {

                throwable.printStackTrace();

            }
        }
    }

    private final class PutGoogleAccountSubscriber extends DefaultSubscriber<Void> {

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
