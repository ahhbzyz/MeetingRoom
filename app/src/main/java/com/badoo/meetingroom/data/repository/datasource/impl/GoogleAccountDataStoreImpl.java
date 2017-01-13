package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.local.GoogleAccountCache;
import com.badoo.meetingroom.data.repository.datasource.intf.GoogleAccountDataStore;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

class GoogleAccountDataStoreImpl implements GoogleAccountDataStore {

    private final GoogleAccountCache googleAccountCache;

    GoogleAccountDataStoreImpl(GoogleAccountCache googleAccountCache) {
        this.googleAccountCache = googleAccountCache;
    }


    @Override
    public Observable<String> getAccountName() {
        return googleAccountCache.get();
    }

    @Override
    public Observable<Void> putAccountName(String accountName) {
        return googleAccountCache.put(accountName);
    }
}
