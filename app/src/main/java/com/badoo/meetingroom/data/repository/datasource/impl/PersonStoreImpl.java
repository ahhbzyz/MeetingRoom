package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.remote.googlepeopleapi.GooglePeopleApi;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonStore;
import com.google.api.services.people.v1.model.Person;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

class PersonStoreImpl implements PersonStore {

    private final GooglePeopleApi mGoogleApi;

    PersonStoreImpl(GooglePeopleApi googleApi) {
        this.mGoogleApi = googleApi;
    }

    @Override
    public Observable<List<Person>> getPersonList() {
        return this.mGoogleApi.getPersonList();
    }

    @Override
    public Observable<Person> getPerson(String personId) {
        return this.mGoogleApi.getPerson(personId);
    }
}
