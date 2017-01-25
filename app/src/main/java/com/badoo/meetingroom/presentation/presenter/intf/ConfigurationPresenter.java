package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.view.MainView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */
public interface ConfigurationPresenter extends Presenter {

    void setView(@NonNull MainView mainView);

    void init();

    void onNoGooglePlayServicesError();

    void storeGoogleAccountName(String accountName);

    void bindPushNotificationsWithRoom(String id);
}
