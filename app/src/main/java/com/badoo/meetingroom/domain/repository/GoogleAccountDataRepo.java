package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public interface GoogleAccountDataRepo {
    Observable<GoogleAccount> getAccountName();
    Observable<Void> putAccountName(String accountName);
}
