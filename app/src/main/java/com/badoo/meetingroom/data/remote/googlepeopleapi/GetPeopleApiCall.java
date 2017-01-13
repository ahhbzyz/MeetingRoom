package com.badoo.meetingroom.data.remote.googlepeopleapi;

import java.util.concurrent.Callable;

import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

import java.util.List;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class GetPeopleApiCall implements Callable<List<Person>>{

    private People mServices = null;

    private GetPeopleApiCall(People services) {
        this.mServices = services;
    }

    static GetPeopleApiCall createGET(People services) {
        return new GetPeopleApiCall(services);
    }

    List<Person> requestSyncCall() throws Exception {
        return connectToApi();
    }

    private List<Person> connectToApi() throws Exception {
        ListConnectionsResponse response = mServices.people().connections()
            .list("people/me")
            .setRequestMaskIncludeField("person.names,person.email_addresses,person.photos")
            .setPageSize(20)
            .execute();
        return response.getConnections();
    }

    @Override
    public List<Person> call() throws Exception {
        return requestSyncCall();
    }

}

