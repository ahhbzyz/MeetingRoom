package com.badoo.meetingroom.presentation.presenter;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface OnGetCredentialListner {
    void onNoGooglePlayServicesError();
    void storeGoogleAccountName(String accountName);
}
