package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.view.GetCredentialView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */
public interface GetCredentialPresenter extends Presenter {
    void setView(@NonNull GetCredentialView getCredentialView);
    void init();
    void onNoGooglePlayServicesError();
    void storeGoogleAccountName(String accountName);
}
