package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.GoogleAccountDataStore;
import com.badoo.meetingroom.data.repository.datasource.GoogleAccountDataStoreFactory;
import com.badoo.meetingroom.domain.entity.GoogleAccount;
import com.badoo.meetingroom.domain.mapper.GoogleAccountMapper;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

@Singleton
public class GoogleAccountDataRepository implements GoogleAccountRepository {

    private GoogleAccountMapper mGoogleAccountMapper;
    private final GoogleAccountDataStore googleAccountDataStore;

    @Inject
    public GoogleAccountDataRepository(GoogleAccountDataStoreFactory googleAccountDataStoreFactory,
                                       GoogleAccountMapper googleAccountMapper) {
        this.mGoogleAccountMapper = googleAccountMapper;
        this.googleAccountDataStore = googleAccountDataStoreFactory.createLocalGoogleAccountNameStore();
    }


    @Override
    public Observable<GoogleAccount> getAccountName() {
        return googleAccountDataStore.getAccountName().map(this.mGoogleAccountMapper::transform);
    }

    @Override
    public Observable<Void> writeAccountName(String accountName) {
        return googleAccountDataStore.writeAccountName(accountName);
    }
}
