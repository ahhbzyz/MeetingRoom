package com.badoo.meetingroom.di.components;

import com.badoo.meetingroom.di.modules.EventsCalendarModule;
import com.badoo.meetingroom.di.modules.GetCredentialModule;
import com.badoo.meetingroom.di.modules.RoomEventsModule;
import com.badoo.meetingroom.di.modules.ApplicationModule;
import com.badoo.meetingroom.presentation.view.activity.BaseActivity;
import com.badoo.meetingroom.presentation.view.activity.EventsCalendarActivity;
import com.badoo.meetingroom.presentation.view.activity.MainActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomEventsActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, EventsCalendarModule.class, RoomEventsModule.class, GetCredentialModule.class})
public interface ApplicationComponent {
  void inject(BaseActivity activity);
  void inject(RoomEventsActivity activity);
  void inject(EventsCalendarActivity activity);
  void inject(MainActivity activity);
}
