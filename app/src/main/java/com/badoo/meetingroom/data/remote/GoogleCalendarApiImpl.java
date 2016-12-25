package com.badoo.meetingroom.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.badoo.meetingroom.data.EventParams;
import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.data.exception.NetworkConnectionException;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class GoogleCalendarApiImpl implements GoogleCalendarApi {

    private Context mContext;

    public GoogleCalendarApiImpl(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The constructor parameters cannot be null");
        }
        this.mContext = context;
    }

    @Override
    public Observable<List<Event>> getEventList(EventsParams params) {
        return Observable.create(subscriber -> {
            if(hasInternetConnection()) {
                try {
                    List<Event> responseEvents = getEventsFromApi(params);
                    if (responseEvents != null) {
                        subscriber.onNext(responseEvents);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(new NetworkConnectionException(e.getCause()));
                }

            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    @Override
    public Observable<Event> insertEvent(EventParams params) {
        return Observable.create(subscriber -> {
            if (hasInternetConnection()) {
                try {
                    Event event = insertEventFromApi(params);
                    if (event != null) {
                        subscriber.onNext(event);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(new NetworkConnectionException(e.getCause()));
                }
            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    private List<Event> getEventsFromApi(EventsParams params) {
        return EventsListApiCall.createGET(params).requestSyncCall();
    }

    private Event insertEventFromApi(EventParams params) {
        return EventInsertApiCall.createINSERT(params).requestSyncCall();
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
