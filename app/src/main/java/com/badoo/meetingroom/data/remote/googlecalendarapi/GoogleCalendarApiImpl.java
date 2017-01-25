package com.badoo.meetingroom.data.remote.googlecalendarapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.badoo.meetingroom.data.exception.NetworkConnectionException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class GoogleCalendarApiImpl implements GoogleCalendarApi {

    private Context mContext;
    private Calendar mServices;

    public GoogleCalendarApiImpl(Context context, Calendar services) {
        if (context == null || services == null) {
            throw new IllegalArgumentException("The constructor parameters cannot be null");
        }
        this.mContext = context;
        this.mServices = services;
    }

    @Override
    public Observable<List<Event>> getEventList(CalendarApiParams params) {

        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }

        return Observable.fromCallable(EventsGetApiCall.create(mServices, params));
    }

    @Override
    public Observable<Event> insertEvent(CalendarApiParams params) {

        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }

        return Observable.fromCallable(EventInsertApiCall.create(mServices, params));
    }

    @Override
    public Observable<Void> deleteEvent(CalendarApiParams params) {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(EventDeleteApiCall.create(mServices, params));
    }

    @Override
    public Observable<Event> updateEvent(CalendarApiParams params) {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(EventUpdateApiCall.create(mServices, params));
    }

    @Override
    public Observable<List<CalendarListEntry>> getCalendarList() {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(GetCalendarListApiCall.create(mServices));
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
