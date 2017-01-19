package com.badoo.meetingroom.di.modules;

import android.content.Context;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.data.repository.BadooPersonRepoImpl;
import com.badoo.meetingroom.data.repository.CalendarListRepoImpl;
import com.badoo.meetingroom.data.repository.GoogleAccountRepoImpl;
import com.badoo.meetingroom.data.repository.LocalEventRepoImpl;
import com.badoo.meetingroom.data.repository.RemoteImageRepoImpl;
import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.domain.repository.BadooPersonRepo;
import com.badoo.meetingroom.domain.repository.CalendarListRepo;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepo;
import com.badoo.meetingroom.domain.repository.LocalEventRepo;
import com.badoo.meetingroom.domain.repository.RemoteImageRepo;
import com.badoo.meetingroom.presentation.Badoo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.PeopleScopes;


import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
@Module
public class ApplicationModule {
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR, PeopleScopes.CONTACTS_READONLY);
    private final AndroidApplication mApplication;
    private GoogleAccountCredential mCredential;
    private Calendar mCalendarServices;
    private People mPeopleServices;

    public ApplicationModule(AndroidApplication application) {
        mApplication = application;

        // Google account credential
        mCredential = GoogleAccountCredential.usingOAuth2(
            mApplication.getApplicationContext(), SCOPES)
            .setBackOff(new ExponentialBackOff());

        // Calendar services
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mCalendarServices = new com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, mCredential)
            .setApplicationName("Meeting Room")
            .build();
        mPeopleServices = new People.Builder(transport, jsonFactory, mCredential)
            .setApplicationName("Meeting Room")
            .build();
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Named("currentRoomCalendar")
    CalendarApiParams provideCalendarApiParams() {
        return new CalendarApiParams(Badoo.getCurrentRoom().getId());
    }

    @Provides
    @Singleton
    GoogleAccountCredential provideGoogleAccountCredential() {
        return mCredential;
    }

    @Provides
    @Singleton
    Calendar provideCalendarServices() {
        return mCalendarServices;
    }

    @Provides
    @Singleton
    People providePeopleServices() {
        return mPeopleServices;
    }

    @Provides
    @Singleton
    LocalEventRepo provideRoomEventRepository(LocalEventRepoImpl roomEventDataRepository) {
        return roomEventDataRepository;
    }

    @Provides
    @Singleton
    GoogleAccountRepo provideGoogleAccountRepository(GoogleAccountRepoImpl googleAccountDataRepository) {
        return googleAccountDataRepository;
    }

    @Provides
    @Singleton
    BadooPersonRepo provideBadooPersonDataRepository(BadooPersonRepoImpl badooPersonDataRepository) {
        return badooPersonDataRepository;
    }

    @Provides
    @Singleton
    RemoteImageRepo provideRemoteImageRepository(RemoteImageRepoImpl remoteImageRepository) {
        return remoteImageRepository;
    }

    @Provides
    @Singleton
    CalendarListRepo provideCalendarListRepository(CalendarListRepoImpl calendarListRepository) {
        return calendarListRepository;
    }
}