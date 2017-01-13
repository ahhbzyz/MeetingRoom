package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.remote.googlepeopleapi.GooglePeopleApi;
import com.badoo.meetingroom.data.remote.googlepeopleapi.GooglePeopleApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonDataStore;
import com.google.api.services.people.v1.People;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

@Singleton
public class PersonDataStoreFactory {

    private final Context mContext;
    private final People mServices;

    @Inject
    PersonDataStoreFactory(@NonNull Context context, @NonNull People services) {
        this.mContext = context.getApplicationContext();
        mServices = services;
    }

    public PersonDataStore createRemotePersonDataStore() {
        GooglePeopleApi googleApi = new GooglePeopleApiImpl(this.mContext, this.mServices);
        return new PersonDataStoreImpl(googleApi);
    }
}
