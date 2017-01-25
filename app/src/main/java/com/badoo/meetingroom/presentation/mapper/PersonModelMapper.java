package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.intf.LocalPerson;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.model.impl.PersonModelImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class PersonModelMapper {

    @Inject
    PersonModelMapper() {

    }

    public PersonModel map(LocalPerson localPerson) {
        PersonModel personModel = null;
        if (localPerson != null) {
            personModel = new PersonModelImpl();
            personModel.setFamilyName(localPerson.getFamilyName());
            personModel.setGivenName(localPerson.getGivenName());
            personModel.setMiddleName(localPerson.getMiddleName());
            personModel.setDisplayName(localPerson.getDisplayName());
            personModel.setEmailAddress(localPerson.getEmailAddress());
            personModel.setAvatarUrl(localPerson.getAvatarUrl());
        }
        return personModel;
    }

    public List<PersonModel> map(List<LocalPerson> localPersonList) {
        final List<PersonModel> personModelList = new ArrayList<>();

        for (LocalPerson localPerson : localPersonList) {

            final PersonModel personModel = map(localPerson);

            if (personModel != null) {
                personModelList.add(personModel);
            }
        }
        return personModelList;
    }
}
