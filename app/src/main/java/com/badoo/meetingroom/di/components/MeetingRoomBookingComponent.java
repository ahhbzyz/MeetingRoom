package com.badoo.meetingroom.di.components;

import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.di.modules.ActivityModule;
import com.badoo.meetingroom.di.modules.DialogModule;
import com.badoo.meetingroom.di.modules.PresentationModule;
import com.badoo.meetingroom.di.modules.UseCaseModule;
import com.badoo.meetingroom.presentation.view.activity.AllRoomsActivity;
import com.badoo.meetingroom.presentation.view.activity.EventsCalendarActivity;
import com.badoo.meetingroom.presentation.view.activity.GetCredentialActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomBookingActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomStatusActivity;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;
import com.badoo.meetingroom.presentation.view.fragment.RoomListFragment;


import dagger.Component;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */

@PerActivity
@Component(
    dependencies = {
        ApplicationComponent.class
    },
    modules = {
        ActivityModule.class,
        UseCaseModule.class,
        PresentationModule.class,
        DialogModule.class
    }
)

public interface MeetingRoomBookingComponent extends ActivityComponent {
    // Activities
    void inject(GetCredentialActivity activity);
    void inject(RoomStatusActivity activity);
    void inject(EventsCalendarActivity activity);
    void inject(RoomBookingActivity activity);
    void inject(AllRoomsActivity activity);
    // Fragments
    void inject(DailyEventsFragment fragment);
    void inject(RoomListFragment fragment);
}