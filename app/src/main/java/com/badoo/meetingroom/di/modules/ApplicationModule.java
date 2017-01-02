package com.badoo.meetingroom.di.modules;

import android.content.Context;

import com.badoo.meetingroom.data.repository.GoogleAccountDataRepository;
import com.badoo.meetingroom.data.repository.RoomEventDataRepository;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;
import com.badoo.meetingroom.di.AndroidApplication;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
@Module
public class ApplicationModule {

    private final AndroidApplication application;
    private final String[] SCOPES = {CalendarScopes.CALENDAR};
    private GoogleAccountCredential mCredential;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
        this.mCredential = GoogleAccountCredential.usingOAuth2(
            application.getApplicationContext(), Collections.singleton(CalendarScopes.CALENDAR))
            .setBackOff(new ExponentialBackOff());
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    GoogleAccountCredential provideGoogleAccountCredential() {
        return mCredential;
    }
}
