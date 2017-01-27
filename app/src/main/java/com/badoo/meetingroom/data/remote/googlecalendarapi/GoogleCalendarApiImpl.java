package com.badoo.meetingroom.data.remote.googlecalendarapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.badoo.meetingroom.data.exception.NetworkConnectionException;
import com.badoo.meetingroom.data.local.FileManager;
import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class GoogleCalendarApiImpl implements GoogleCalendarApi {

    private Context mContext;
    private Calendar mServices;
    private FileManager mFileManager;

    @Inject
    public GoogleCalendarApiImpl(Context context, Calendar services, FileManager fileManager) {
        if (context == null || services == null) {
            throw new IllegalArgumentException("The constructor parameters cannot be null");
        }
        mContext = context;
        mServices = services;
        mFileManager = fileManager;
    }

    @Override
    public Observable<List<Event>> getEventList(CalendarApiParams params) {

        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }

        return Observable.fromCallable(GetEventsApiCall.create(mServices, params));
    }

    @Override
    public Observable<Event> insertEvent(CalendarApiParams params) {

        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }

        return Observable.fromCallable(InsertEventApiCall.create(mServices, params));
    }

    @Override
    public Observable<Void> deleteEvent(CalendarApiParams params) {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(DeleteEventApiCall.create(mServices, params));
    }

    @Override
    public Observable<Event> updateEvent(CalendarApiParams params) {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(UpdateEventApiCall.create(mServices, params));
    }

    @Override
    public Observable<List<CalendarListEntry>> getCalendarList() {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(GetCalendarListApiCall.create(mServices));
    }

    @Override
    public Observable<Channel> bindPushNotifications(CalendarApiParams params) {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(BindPushNotificationsApiCall.bind(mContext, mServices, mFileManager, params));
    }


    private boolean hasInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager =
            (ConnectivityManager)this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }
}
