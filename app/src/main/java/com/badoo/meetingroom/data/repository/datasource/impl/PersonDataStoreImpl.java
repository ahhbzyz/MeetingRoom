package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.remote.googlepeopleapi.GooglePeopleApi;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonDataStore;
import com.google.api.services.people.v1.model.Person;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

class PersonDataStoreImpl implements PersonDataStore {

    private final GooglePeopleApi mGoogleApi;

    PersonDataStoreImpl(GooglePeopleApi googleApi) {
        this.mGoogleApi = googleApi;
    }

    @Override
    public Observable<List<Person>> getPersonList() {
        return this.mGoogleApi.getPersonList();
    }
}
