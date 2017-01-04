package com.badoo.meetingroom.di.modules;

import android.content.Context;

import com.badoo.meetingroom.data.repository.GoogleAccountDataRepository;
import com.badoo.meetingroom.data.repository.RoomEventDataRepository;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;
import com.badoo.meetingroom.di.AndroidApplication;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
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
    private GoogleAccountCredential mCredential;
    private Calendar mServices;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;

        // Google account credential
        this.mCredential = GoogleAccountCredential.usingOAuth2(
            application.getApplicationContext(), Collections.singleton(CalendarScopes.CALENDAR))
            .setBackOff(new ExponentialBackOff());

        // Calendar services
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        this.mServices = new com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, mCredential)
            .setApplicationName("Meeting Room")
            .build();
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

    @Provides
    @Singleton
    Calendar provideCalendarServices() {
        return mServices;
    }
}
