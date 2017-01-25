package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.impl.LocalPersonImpl;
import com.badoo.meetingroom.domain.entity.intf.LocalPerson;
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
public class LocalPersonMapper {

    @Inject
    LocalPersonMapper() {

    }

    public LocalPerson map(Person person) {
        LocalPerson localPerson = null;
        if (person != null) {
            localPerson = new LocalPersonImpl();
            if (person.getNames() != null && !person.getNames().isEmpty()) {

                Name name = person.getNames().get(0);

                if (name.getFamilyName() != null) {
                    localPerson.setFamilyName(name.getFamilyName());
                }

                if (name.getGivenName() != null) {
                    localPerson.setGivenName(name.getGivenName());
                }

                if (name.getMiddleName() != null) {
                    localPerson.setMiddleName(name.getMiddleName());
                }

                if (name.getDisplayName() != null) {
                    localPerson.setDisplayName(name.getDisplayName());
                }
            }

            if (person.getEmailAddresses() != null && !person.getEmailAddresses().isEmpty()) {
                localPerson.setEmailAddress(person.getEmailAddresses().get(0).getValue());
            }

            if (person.getPhotos() != null && !person.getPhotos().isEmpty()) {
                localPerson.setAvatarUrl(person.getPhotos().get(0).getUrl());
            }
        }
        return localPerson;
    }

    public List<LocalPerson> map(List<Person> personList) {
        final List<LocalPerson> localPersonList = new ArrayList<>();

        for (Person person : personList) {

            final LocalPerson localPerson = map(person);

            if (localPerson != null) {
                localPersonList.add(localPerson);
            }
        }
        return localPersonList;
    }
}
