package com.badoo.meetingroom.di.modules;

import android.content.Context;

import com.badoo.meetingroom.data.repository.BadooPersonDataRepoImpl;
import com.badoo.meetingroom.data.repository.RoomEventDataRepoImpl;
import com.badoo.meetingroom.data.repository.GoogleAccountDataRepoImpl;
import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.badoo.meetingroom.domain.repository.BadooPersonDataRepo;
import com.badoo.meetingroom.domain.repository.RoomEventDataRepo;
import com.badoo.meetingroom.domain.repository.GoogleAccountDataRepo;
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
    private Calendar mCalendarServices;
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR, PeopleScopes.CONTACTS_READONLY);

    private People mPeopleServices;


    public ApplicationModule(AndroidApplication application) {
        this.application = application;

        // Google account credential
        this.mCredential = GoogleAccountCredential.usingOAuth2(
            application.getApplicationContext(), SCOPES)
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
        return application;
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
    RoomEventDataRepo provideRoomEventRepository(RoomEventDataRepoImpl roomEventDataRepository) {
        return roomEventDataRepository;
    }

    @Provides
    @Singleton
    GoogleAccountDataRepo provideGoogleAccountRepository(GoogleAccountDataRepoImpl googleAccountDataRepository) {
        return googleAccountDataRepository;
    }

    @Provides
    @Singleton
    BadooPersonDataRepo provideBadooPersonDataRepository(BadooPersonDataRepoImpl badooPersonDataRepository) {
        return badooPersonDataRepository;
    }
}