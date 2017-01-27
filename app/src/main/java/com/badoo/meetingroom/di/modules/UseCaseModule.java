package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.domain.interactor.BindPushNotifications;
import com.badoo.meetingroom.domain.interactor.GetRoomList;
import com.badoo.meetingroom.domain.interactor.GetRoomMap;
import com.badoo.meetingroom.domain.interactor.event.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.event.GetEvents;
import com.badoo.meetingroom.domain.interactor.googleaccount.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.GetPersons;
import com.badoo.meetingroom.domain.interactor.event.InsertEvent;
import com.badoo.meetingroom.domain.interactor.googleaccount.PutGoogleAccount;
import com.badoo.meetingroom.domain.interactor.event.UpdateEvent;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@Module
public class UseCaseModule {

    public UseCaseModule() {}

    @Provides
    @Named(GetGoogleAccount.NAME)
    GetGoogleAccount provideGetGoogleAccountUseCase(GetGoogleAccount getGoogleAccount) {
        return getGoogleAccount;
    }

    @Provides
    @Named(PutGoogleAccount.NAME)
    PutGoogleAccount providePutGoogleAccountUseCase(PutGoogleAccount putGoogleAccount) {
        return putGoogleAccount;
    }

    @Provides
    @Named(GetEvents.NAME)
    GetEvents provideGetEventListUseCase(GetEvents getEvents) {
        return getEvents;
    }

    @Provides
    @Named(InsertEvent.NAME)
    InsertEvent provideInsertEventUseCase(InsertEvent insertEvent) {
        return insertEvent;
    }

    @Provides
    @Named(DeleteEvent.NAME)
    DeleteEvent provideDeleteEventUseCase(DeleteEvent deleteEvent) {
        return deleteEvent;
    }

    @Provides
    @Named(UpdateEvent.NAME)
    UpdateEvent provideUpdateEventUseCase(UpdateEvent updateEvent) {
        return updateEvent;
    }

    @Provides
    @Named(GetPersons.NAME)
    GetPersons provideGetPersonsUseCase(GetPersons getPersons) {
        return getPersons;
    }

    @Provides
    @Named(GetRoomMap.NAME)
    GetRoomMap provideGetRoomMapUseCase(GetRoomMap getRoomMap) {
        return getRoomMap;
    }

    @Provides
    @Named(GetRoomList.NAME)
    GetRoomList provideGetRoomListUseCase(GetRoomList getRoomList) {
        return getRoomList;
    }


    @Provides
    @Named(BindPushNotifications.NAME)
    BindPushNotifications provideBindPushNotificationsUseCase(BindPushNotifications bindPushNotifications) {
        return bindPushNotifications;
    }
}
