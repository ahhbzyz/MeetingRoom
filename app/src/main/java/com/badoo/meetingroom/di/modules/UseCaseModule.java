package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.domain.interactor.event.DeleteEvent;
import com.badoo.meetingroom.domain.interactor.GetAvatar;
import com.badoo.meetingroom.domain.interactor.GetCalendarList;
import com.badoo.meetingroom.domain.interactor.event.GetCalendarEvents;
import com.badoo.meetingroom.domain.interactor.event.GetRoomEvents;
import com.badoo.meetingroom.domain.interactor.GetGoogleAccount;
import com.badoo.meetingroom.domain.interactor.GetPerson;
import com.badoo.meetingroom.domain.interactor.GetPersons;
import com.badoo.meetingroom.domain.interactor.event.InsertEvent;
import com.badoo.meetingroom.domain.interactor.PutGoogleAccount;
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
    @Named(GetRoomEvents.NAME)
    GetRoomEvents provideGetRoomEventListUseCase(GetRoomEvents getEvents) {
        return getEvents;
    }

    @Provides
    @Named(GetCalendarEvents.NAME)
    GetCalendarEvents provideGetCalendarEventListUseCase(GetCalendarEvents getEvents) {
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
    @Named(GetPerson.NAME)
    GetPerson provideGetPersonUseCase(GetPerson getPerson) {
        return getPerson;
    }

    @Provides
    @Named(GetAvatar.NAME)
    GetAvatar provideGetAvatarUseCase(GetAvatar getAvatar) {
        return getAvatar;
    }

    @Provides
    @Named(GetCalendarList.NAME)
    GetCalendarList provideGetCalendarListUseCase(GetCalendarList getCalendarList) {
        return getCalendarList;
    }
}
