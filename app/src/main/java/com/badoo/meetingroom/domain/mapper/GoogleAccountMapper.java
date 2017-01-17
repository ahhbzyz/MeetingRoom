package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;
import com.badoo.meetingroom.domain.entity.impl.GoogleAccountImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

@Singleton
public class GoogleAccountMapper {

    @Inject
    GoogleAccountMapper() {}

    public GoogleAccount map(String accountName) {
        GoogleAccount account = null;
        if (accountName != null) {
            account = new GoogleAccountImpl(accountName);
        }
        return account;
    }
}
