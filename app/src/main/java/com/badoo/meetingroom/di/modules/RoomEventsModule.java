package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.data.repository.RoomEventDataRepository;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.domain.repository.RoomEventRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@Module
public class RoomEventsModule {

    public RoomEventsModule() {}

    @Provides
    @Named("getRoomEventList")
    GetRoomEventList provideGetRoomEventListUseCase(GetRoomEventList getRoomEventList) {
        return getRoomEventList;
    }

    @Provides
    @Singleton
    RoomEventRepository provideRoomEventRepository(RoomEventDataRepository roomEventDataRepository) {
        return roomEventDataRepository;
    }
}
