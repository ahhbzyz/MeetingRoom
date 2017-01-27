package com.badoo.meetingroom.data.remote.googlecalendarapi;

import android.util.Log;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;

import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 26/01/2017.
 */

public class UnbindPushNotificationsApiCall implements Callable<Void> {

    private Calendar mServices = null;
    private final String mUuId;
    private final String mResourceId;

    private UnbindPushNotificationsApiCall(Calendar services, String uuid, String resourceId) {
        mServices = services;
        mUuId = uuid;
        mResourceId = resourceId;
    }

    static UnbindPushNotificationsApiCall unbind(Calendar services, String uuid, String resourceId) {
        return new UnbindPushNotificationsApiCall(services, uuid, resourceId);
    }

    public Void requestSyncCall() throws Exception {
        return connectToApi();
    }

    private Void connectToApi() throws Exception {

        Channel request = new Channel()
            .setId(mUuId)
            .setResourceId(mResourceId);

        mServices.channels().stop(request).execute();

        Log.d("Unbind notifications", "Channel id: " + mUuId + " resourcesId: " + mResourceId);

        return null;
    }

    @Override
    public Void call() throws Exception {
        return requestSyncCall();
    }
}
