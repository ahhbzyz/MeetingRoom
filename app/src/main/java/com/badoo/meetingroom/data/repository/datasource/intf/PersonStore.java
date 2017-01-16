package com.badoo.meetingroom.data.repository.datasource.intf;

import com.google.api.services.people.v1.model.Person;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public interface PersonStore {
    Observable<List<Person>> getPersonList();
    Observable<Person> getPerson(String personId);
}
