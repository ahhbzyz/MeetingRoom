package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.data.repository.GoogleAccountDataRepository;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.PutGoogleAccount;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

@Module
public class GetCredentialModule {
    public GetCredentialModule() {}

    @Provides
    @Named("getGoogleAccount")
    GetGoogleAccount provideGetGoogleAccountUseCase(GetGoogleAccount getGoogleAccount) {
        return getGoogleAccount;
    }

    @Provides
    @Named("putGoogleAccount")
    PutGoogleAccount providePutGoogleAccountUseCase(PutGoogleAccount putGoogleAccount) {
        return putGoogleAccount;
    }

    @Provides
    @Singleton
    GoogleAccountRepository provideGoogleAccountRepository(GoogleAccountDataRepository googleAccountDataRepository) {
        return googleAccountDataRepository;
    }
}
