package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.GetCredentialView;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */
public interface GetCredentialPresenter extends Presenter {
    void setView(@NonNull GetCredentialView getCredentialView);
    void onNoGooglePlayServicesError();
    void storeGoogleAccountName(String accountName);
}
