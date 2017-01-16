package com.badoo.meetingroom.data.remote.googlepeopleapi;

import com.google.api.services.people.v1.model.Person;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public interface GooglePeopleApi {
    Observable<List<Person>> getPersonList();
    Observable<Person> getPerson(String personId);
}
