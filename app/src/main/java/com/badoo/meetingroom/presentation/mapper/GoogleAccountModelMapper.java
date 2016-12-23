package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.GoogleAccount;
import com.badoo.meetingroom.presentation.model.GoogleAccountModel;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GoogleAccountModelMapper {

    @Inject
    public GoogleAccountModelMapper() {

    }

    public GoogleAccountModel map(GoogleAccount account) {

        GoogleAccountModel accountModel = null;

        if (account != null && account.getAccountName() != null) {
            accountModel = new GoogleAccountModel(account.getAccountName());
        }
        return accountModel;
    }
}
