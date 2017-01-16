package com.badoo.meetingroom.data.repository.datasource.intf;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public interface GoogleAccountStore {

    Observable<String> getAccountName();
    Observable<Void> putAccountName(String accountName);
}
