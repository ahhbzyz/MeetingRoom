package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.interactor.GetRoomEventList;
import com.badoo.meetingroom.domain.interactor.UseCase;

import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

@Module
public class RoomEventModule {

    public RoomEventModule() {}

    @Provides
    @Named("getRoomEventList")
    GetRoomEventList provideGetRoomEventListUseCase(GetRoomEventList getRoomEventList) {
        return getRoomEventList;
    }
}
