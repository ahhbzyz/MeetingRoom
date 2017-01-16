package com.badoo.meetingroom.data.remote.googlepeopleapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.badoo.meetingroom.data.exception.NetworkConnectionException;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Person;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class GooglePeopleApiImpl implements GooglePeopleApi {


    private Context mContext;
    private People mServices;

    public GooglePeopleApiImpl(Context context, People services) {
        if (context == null || services == null) {
            throw new IllegalArgumentException("The constructor parameters cannot be null");
        }
        this.mContext = context;
        this.mServices = services;
    }

    @Override
    public Observable<List<Person>> getPersonList() {
        return Observable.create(subscriber -> {
            if(hasInternetConnection()) {
                try {
                    List<Person> result = getPersonListFromApi(mServices);
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
    public Observable<Person> getPerson(String personId) {
        return Observable.create(subscriber -> {
            if(hasInternetConnection()) {
                try {
                    Person result = getPersonFromApi(mServices, personId);
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

    private List<Person> getPersonListFromApi(People services) throws Exception {
        return GetPeopleApiCall.createGET(services).requestSyncCall();
    }

    private Person getPersonFromApi(People services, String personId) throws Exception {
        return GetPersonApiCall.createGET(services, personId).requestSyncCall();
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
