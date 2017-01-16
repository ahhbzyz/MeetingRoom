package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.impl.BadooPersonImpl;
import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

@Singleton
public class BadooPersonMapper {

    @Inject
    BadooPersonMapper() {

    }

    public BadooPerson map(Person person) {
        BadooPerson badooPerson = null;
        if (person != null) {
            badooPerson = new BadooPersonImpl();
            if (person.getNames() != null && !person.getNames().isEmpty()) {

                Name name = person.getNames().get(0);

                if (name.getFamilyName() != null) {
                    badooPerson.setFamilyName(name.getFamilyName());
                }

                if (name.getGivenName() != null) {
                    badooPerson.setGivenName(name.getGivenName());
                }

                if (name.getMiddleName() != null) {
                    badooPerson.setMiddleName(name.getMiddleName());
                }

                if (name.getDisplayName() != null) {
                    badooPerson.setDisplayName(name.getDisplayName());
                }
            }

            if (person.getEmailAddresses() != null && !person.getEmailAddresses().isEmpty()) {
                badooPerson.setEmailAddress(person.getEmailAddresses().get(0).getValue());
            }

            if (person.getPhotos() != null && !person.getPhotos().isEmpty()) {
                badooPerson.setAvatarUrl(person.getPhotos().get(0).getUrl());
            }
        }
        return badooPerson;
    }

    public List<BadooPerson> map(List<Person> personList) {
        final List<BadooPerson> badooPersonList = new ArrayList<>();

        for (Person person : personList) {

            final BadooPerson badooPerson = map(person);

            if (badooPerson != null) {
                badooPersonList.add(badooPerson);
            }
        }
        return badooPersonList;
    }
}
