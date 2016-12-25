package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.GoogleAccount;
import com.badoo.meetingroom.domain.entity.GoogleAccountImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

@Singleton
public class GoogleAccountDataMapper {

    @Inject
    public GoogleAccountDataMapper() {}

    public GoogleAccount transform(String accountName) {
        GoogleAccount account = null;
        if (accountName != null) {
            account = new GoogleAccountImpl(accountName);
        }
        return account;
    }
}