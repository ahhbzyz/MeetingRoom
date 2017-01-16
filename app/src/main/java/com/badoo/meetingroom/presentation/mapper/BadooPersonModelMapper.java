package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.badoo.meetingroom.presentation.model.BadooPersonModel;
import com.badoo.meetingroom.presentation.model.BadooPersonModelImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class BadooPersonModelMapper {

    @Inject
    BadooPersonModelMapper() {

    }

    public BadooPersonModel map(BadooPerson badooPerson) {
        BadooPersonModel badooPersonModel = null;
        if (badooPerson != null) {

            badooPersonModel = new BadooPersonModelImpl();

            if (badooPerson.getFamilyName() != null) {
                badooPersonModel.setFamilyName(badooPerson.getFamilyName());
            }

            if (badooPerson.getGivenName() != null) {
                badooPersonModel.setGivenName(badooPerson.getGivenName());
            }

            if (badooPerson.getMiddleName() != null) {
                badooPersonModel.setMiddleName(badooPerson.getMiddleName());
            }

            if (badooPerson.getDisplayName() != null) {
                badooPersonModel.setDisplayName(badooPerson.getDisplayName());
            }

            if (badooPerson.getEmailAddress() != null) {
                badooPersonModel.setEmailAddress(badooPerson.getEmailAddress());
            }

            if (badooPerson.getAvatarUrl() != null) {
                badooPersonModel.setAvatarUrl(badooPerson.getAvatarUrl());
            }
        }
        return badooPersonModel;
    }

    public List<BadooPersonModel> map(List<BadooPerson> badooPersonList) {
        final List<BadooPersonModel> badooPersonModelList = new ArrayList<>();

        for (BadooPerson badooPerson : badooPersonList) {

            final BadooPersonModel badooPersonModel = map(badooPerson);

            if (badooPersonModel != null) {
                badooPersonModelList.add(badooPersonModel);
            }
        }
        return badooPersonModelList;
    }
}
