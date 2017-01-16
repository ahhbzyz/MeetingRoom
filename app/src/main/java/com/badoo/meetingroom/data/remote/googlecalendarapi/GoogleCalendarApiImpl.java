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
    public Observable<List<Event>> getEventList(Event event) {
        return Observable.create(subscriber -> {
            if(hasInternetConnection()) {
                try {
                    List<Event> result = getEventsFromApi(mServices, event);
                    if (result != null) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    @Override
    public Observable<Event> insertEvent(Event event) {
        return Observable.create(subscriber -> {
            if (hasInternetConnection()) {
                try {
                    Event result = insertEventFromApi(mServices, event);
                    if (event != null) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    @Override
    public Observable<Void> deleteEvent(Event event) {
        return Observable.create(subscriber -> {
            if (hasInternetConnection()) {
                try {
                    subscriber.onNext(deleteEventFromApi(mServices, event));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    @Override
    public Observable<Event> updateEvent(Event event) {
        return Observable.create(subscriber -> {
            if (hasInternetConnection()) {
                try {
                    Event result = updateEventFromApi(mServices, event);
                    if (event != null) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    @Override
    public Observable<List<CalendarListEntry>> getCalendarList() {
        return Observable.create(subscriber -> {
            if(hasInternetConnection()) {
                try {
                    List<CalendarListEntry> result = getCalendarListFromApi(mServices);
                    if (result != null) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    private List<Event> getEventsFromApi(Calendar services, Event event) throws Exception {
        return EventsGetApiCall.createGET(services, event).requestSyncCall();
    }

    private Event insertEventFromApi(Calendar services, Event event) throws Exception {
        return EventInsertApiCall.createINSERT(services, event).requestSyncCall();
    }

    private Void deleteEventFromApi(Calendar services, Event event) throws Exception {
        return EventDeleteApiCall.createDelete(services, event).requestSyncCall();
    }

    private Event updateEventFromApi(Calendar service, Event event) throws Exception {
        return EventUpdateApiCall.createUpdate(service, event).requestSyncCall();
    }

    private List<CalendarListEntry> getCalendarListFromApi(Calendar service) throws Exception {
        return GetCalendarListApiCall.createGET(service).requestSyncCall();
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
