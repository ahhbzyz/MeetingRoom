package com.badoo.meetingroom.di.components;

import com.badoo.meetingroom.di.modules.EventsCalendarModule;
import com.badoo.meetingroom.di.modules.GetCredentialModule;
import com.badoo.meetingroom.di.modules.RoomEventsModule;
import com.badoo.meetingroom.di.modules.ApplicationModule;
import com.badoo.meetingroom.presentation.view.activity.AllRoomsActivity;
import com.badoo.meetingroom.presentation.view.activity.BaseActivity;
import com.badoo.meetingroom.presentation.view.activity.EventsCalendarActivity;
import com.badoo.meetingroom.presentation.view.activity.GetCredentialActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomBookingActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomEventsActivity;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;
import com.badoo.meetingroom.presentation.view.fragment.RoomListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, EventsCalendarModule.class, RoomEventsModule.class, GetCredentialModule.class})
public interface ApplicationComponent {
  void inject(BaseActivity activity);
  void inject(RoomEventsActivity activity);
  void inject(EventsCalendarActivity activity);
  void inject(GetCredentialActivity activity);
  void inject(DailyEventsFragment fragment);
  void inject(RoomBookingActivity activity);
  void inject(AllRoomsActivity activity);
  void inject(RoomListFragment fragment);
}
