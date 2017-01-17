package com.badoo.meetingroom.di.components;

import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.di.modules.ActivityModule;
import com.badoo.meetingroom.di.modules.FontAssetModule;
import com.badoo.meetingroom.di.modules.PresentationModule;
import com.badoo.meetingroom.di.modules.UseCaseModule;
import com.badoo.meetingroom.presentation.view.activity.AllRoomActivity;
import com.badoo.meetingroom.presentation.view.activity.EventsCalendarActivity;
import com.badoo.meetingroom.presentation.view.activity.MainActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomBookingActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomStatusActivity;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;
import com.badoo.meetingroom.presentation.view.fragment.EventCreatorDialogFragment;
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
        FontAssetModule.class
    }
)

public interface MeetingRoomBookingComponent extends ActivityComponent {
    // Activities
    void inject(MainActivity activity);
    void inject(RoomStatusActivity activity);
    void inject(EventsCalendarActivity activity);
    void inject(RoomBookingActivity activity);
    void inject(AllRoomActivity activity);
    // Fragments
    void inject(DailyEventsFragment fragment);
    void inject(RoomListFragment fragment);
    void inject(EventCreatorDialogFragment fragment);
}