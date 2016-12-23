package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.WriteGoogleAccount;

import javax.inject.Named;

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
    @Named("writeGoogleAccount")
    WriteGoogleAccount provideWriteGoogleAccountUseCase(WriteGoogleAccount writeGoogleAccount) {
        return writeGoogleAccount;
    }
}
