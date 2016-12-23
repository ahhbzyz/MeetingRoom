package com.badoo.meetingroom.presentation.presenter;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.GetCredentialView;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GetCredentialPresenterImpl implements GetCredentialPresenter, OnGetCredentialListner {

    private GetCredentialView mGetCredentialView;

    @Inject
    public GetCredentialPresenterImpl() {}

    @Override
    public void init() {
        acquireGooglePlayServices();
        chooseAccount();
    }
    private void acquireGooglePlayServices() {
        if (! mGetCredentialView.isGooglePlayServicesAvailable()) {
            int code = mGetCredentialView.getGooglePlayServicesStatusCode();
            if (!mGetCredentialView.checkGooglePlayServicesAvailability(code)) {
                mGetCredentialView.showGooglePlayServicesAvailabilityErrorDialog(code);
            }
        }
    }

    private void chooseAccount() {
        if (mGetCredentialView.hasPermissionToAccessContacts()) {
            if (mGetCredentialView.getAccountNameFromLocal() != null) {
                //Navigate to Next
            } else {
                mGetCredentialView.showChooseAccountDialog();
            }
        } else {
            mGetCredentialView.showRequestPermissionsDialog();
        }
    }


    @Override
    public void setView(@NonNull GetCredentialView getCredentialView) {
        this.mGetCredentialView = getCredentialView;
    }

    @Override
    public void onNoGooglePlayServicesError() {
        mGetCredentialView.showNoGooglePlayServicesToast();
    }

    @Override
    public void storeGoogleAccountName(String accountName) {
        mGetCredentialView.storeGoogleAccountName(accountName);
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
