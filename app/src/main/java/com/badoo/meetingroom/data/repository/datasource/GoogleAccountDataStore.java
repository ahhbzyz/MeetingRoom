package com.badoo.meetingroom.data.repository.datasource;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public interface GoogleAccountDataStore {

    Observable<String> getAccountName();

    Observable<Void> writeAccountName(String accountName);
}
