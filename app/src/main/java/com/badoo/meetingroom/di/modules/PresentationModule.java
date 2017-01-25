package com.badoo.meetingroom.di.modules;

import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.presentation.presenter.impl.DailyEventsPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.EventCreatorDialogPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.EventsCalendarPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.MainPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.RoomBookingPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.RoomListFragmentPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.RoomStatusPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.impl.RoomListPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.EventCreatorDialogPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.EventsCalendarPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.MainPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.RoomStatusPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.RoomsPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */

@Module
public class PresentationModule {

    public PresentationModule() {}

    @Provides
    @PerActivity
    MainPresenter provideGetCredentialPresenter(MainPresenterImpl getCredentialPresenterImpl) {
        return getCredentialPresenterImpl;
    }

    @Provides
    @PerActivity
    RoomStatusPresenter provideRoomStatusPresenter(RoomStatusPresenterImpl roomStatusPresenterImpl) {
        return roomStatusPresenterImpl;
    }

    @Provides
    @PerActivity
    EventsCalendarPresenter provideEventsCalendarPresenter(EventsCalendarPresenterImpl eventsCalendarPresenterImpl) {
        return eventsCalendarPresenterImpl;
    }

    @Provides
    @PerActivity
    DailyEventsPresenter provideDailyEventsPresenter(DailyEventsPresenterImpl dailyEventsPresenterImpl) {
        return dailyEventsPresenterImpl;
    }

    @Provides
    @PerActivity
    RoomBookingPresenter provideRoomBookingPresenter(RoomBookingPresenterImpl roomBookingPresenterImpl) {
        return roomBookingPresenterImpl;
    }

    @Provides
    @PerActivity
    RoomsPresenter provideRoomsPresenter(RoomListPresenterImpl roomListPresenterImpl) {
        return roomListPresenterImpl;
    }

    @Provides
    @PerActivity
    RoomListPresenter provideRoomListPresenterImpl(RoomListFragmentPresenterImpl roomListFragmentPresenterImpl) {
        return roomListFragmentPresenterImpl;
    }

    @Provides
    @PerActivity
    EventCreatorDialogPresenter provideEventCreatorDialogPresenterImpl(EventCreatorDialogPresenterImpl eventCreatorDialogPresenterImpl) {
        return eventCreatorDialogPresenterImpl;
    }
}
