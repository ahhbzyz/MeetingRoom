package com.badoo.meetingroom.data.remote.googlecalendarapi;

import android.content.Context;
import android.util.Log;

import com.badoo.meetingroom.data.local.FileManager;
import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 19/01/2017.
 */

class BindPushNotificationsApiCall implements Callable<Channel> {


    private Calendar mServices = null;
    private final CalendarApiParams mParams;
    private final FileManager mFileManager;
    private final Context mContext;
    private final String NOTIFICATIONS_CHANNEL_INFO = "notifications_channel_info";

    private BindPushNotificationsApiCall(Context mContext, FileManager fileManager, Calendar services, CalendarApiParams params) {
        this.mContext = mContext;
        mServices = services;
        mParams = params;
        mFileManager = fileManager;
    }

    static BindPushNotificationsApiCall bind(Context context, Calendar services, FileManager fileManager, CalendarApiParams params) {
        return new BindPushNotificationsApiCall(context, fileManager, services, params);
    }

    public Channel requestSyncCall() throws Exception {
        return connectToApi();
    }

    private Channel connectToApi() throws Exception {


        // get previous channel info
        String channelId = mFileManager.getFileNameFromPreferences(mContext, NOTIFICATIONS_CHANNEL_INFO, mParams.getRoomName()+ "_channelId");
        String resourceId = mFileManager.getFileNameFromPreferences(mContext, NOTIFICATIONS_CHANNEL_INFO, mParams.getRoomName() + "_resourceId");

        String uniqueID = UUID.randomUUID().toString();

        while (uniqueID.equals(channelId)) {
            uniqueID = UUID.randomUUID().toString();
        }

        Channel request = new Channel()
            .setId(uniqueID)
            .setType("web_hook")
            .setToken(mParams.getRoomName())
            .setAddress("https://badoomeetingroom.000webhostapp.com/notifications");

        Channel channel = mServices.events().watch(mParams.getCalendarId(), request).execute();

        // If bind successfully
        if (channel != null && channel.getId() != null && channel.getResourceId() != null) {

            Log.d("Bind notifications", "Channel id: " + channel.getId() + " resourcesId: " + channel.getResourceId());

            // Unbind previous channel
            if (channelId != null && resourceId != null) {
                UnbindPushNotificationsApiCall.unbind(mServices, channelId, resourceId).call();
            }

            // put current channel info
            mFileManager.putFileNameToPreferences(mContext, NOTIFICATIONS_CHANNEL_INFO, channel.getToken() + "_channelId", channel.getId());
            mFileManager.putFileNameToPreferences(mContext, NOTIFICATIONS_CHANNEL_INFO, channel.getToken() + "_resourceId", channel.getResourceId());
        }

        return channel;
    }

    @Override
    public Channel call() throws Exception {
        return requestSyncCall();
    }
}
