package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.intf.GoogleAccountDataStore;
import com.badoo.meetingroom.data.repository.datasource.impl.GoogleAccountDataStoreFactory;
import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;
import com.badoo.meetingroom.domain.mapper.GoogleAccountDataMapper;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

@Singleton
public class GoogleAccountDataRepository implements GoogleAccountRepository {

    private GoogleAccountDataMapper mGoogleAccountDataMapper;
    private final GoogleAccountDataStore googleAccountDataStore;

    @Inject
    public GoogleAccountDataRepository(GoogleAccountDataStoreFactory googleAccountDataStoreFactory,
                                       GoogleAccountDataMapper googleAccountDataMapper) {
        this.mGoogleAccountDataMapper = googleAccountDataMapper;
        this.googleAccountDataStore = googleAccountDataStoreFactory.createLocalGoogleAccountNameStore();
    }


    @Override
    public Observable<GoogleAccount> getAccountName() {
        return googleAccountDataStore.getAccountName().map(this.mGoogleAccountDataMapper::transform);
    }

    @Override
    public Observable<Void> putAccountName(String accountName) {
        return googleAccountDataStore.putAccountName(accountName);
    }
}
