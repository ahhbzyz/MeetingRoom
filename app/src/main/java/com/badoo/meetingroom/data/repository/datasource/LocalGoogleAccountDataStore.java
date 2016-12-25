package com.badoo.meetingroom.data.repository.datasource;

import com.badoo.meetingroom.data.local.GoogleAccountCache;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class LocalGoogleAccountDataStore implements GoogleAccountDataStore {

    private final GoogleAccountCache googleAccountCache;

    LocalGoogleAccountDataStore(GoogleAccountCache googleAccountCache) {
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
