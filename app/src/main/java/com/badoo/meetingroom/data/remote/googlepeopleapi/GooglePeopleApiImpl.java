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
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(GetPeopleApiCall.create(mServices));
    }

    @Override
    public Observable<Person> getPerson(String personId) {
        if (!hasInternetConnection()) {
            return Observable.error(new NetworkConnectionException());
        }
        return Observable.fromCallable(GetPersonApiCall.create(mServices, personId));
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
