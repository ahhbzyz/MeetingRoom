package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.domain.interactor.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.GetEvents;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.InsertEvent;
import com.badoo.meetingroom.domain.interactor.PutGoogleAccount;
import com.badoo.meetingroom.domain.interactor.UpdateEvent;

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
    GetEvents provideGetRoomEventListUseCase(GetEvents getEvents) {
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

}
