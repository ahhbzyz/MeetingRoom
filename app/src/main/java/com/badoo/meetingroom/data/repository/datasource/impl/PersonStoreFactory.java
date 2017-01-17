package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.remote.googlepeopleapi.GooglePeopleApi;
import com.badoo.meetingroom.data.remote.googlepeopleapi.GooglePeopleApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonStore;
import com.google.api.services.people.v1.People;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

@Singleton
public class PersonStoreFactory {

    private final Context mContext;
    private final People mServices;

    @Inject
    PersonStoreFactory(@NonNull Context context, @NonNull People services) {
        this.mContext = context.getApplicationContext();
        mServices = services;
    }

    public PersonStore createRemotePersonStore() {
        GooglePeopleApi googleApi = new GooglePeopleApiImpl(this.mContext, this.mServices);
        return new PersonStoreImpl(googleApi);
    }
}
