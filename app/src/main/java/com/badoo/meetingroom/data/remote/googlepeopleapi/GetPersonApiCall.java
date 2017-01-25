package com.badoo.meetingroom.data.remote.googlepeopleapi;

import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Person;

import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

class GetPersonApiCall implements Callable<Person> {

    private String mPersonId;
    private People mServices = null;

    private GetPersonApiCall(People services, String personId) {
        this.mServices = services;
        this.mPersonId = personId;
    }

    static GetPersonApiCall create(People services, String personId) {
        return new GetPersonApiCall(services, personId);
    }

    Person requestSyncCall() throws Exception {
        return connectToApi();
    }

    private Person connectToApi() throws Exception {
        return mServices.people()
            .get(mPersonId)
            .execute();
    }

    @Override
    public Person call() throws Exception {
        return requestSyncCall();
    }

}
