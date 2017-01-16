package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.impl.GoogleAccountStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.GoogleAccountStore;
import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;
import com.badoo.meetingroom.domain.mapper.GoogleAccountMapper;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepo;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

@Singleton
public class GoogleAccountRepoImpl implements GoogleAccountRepo {

    private GoogleAccountMapper mGoogleAccountDataMapper;
    private final GoogleAccountStore googleAccountDataStore;

    @Inject
    GoogleAccountRepoImpl(GoogleAccountStoreFactory googleAccountDataStoreFactory,
                          GoogleAccountMapper googleAccountDataMapper) {
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
