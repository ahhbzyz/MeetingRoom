package com.badoo.meetingroom.di.components;

import com.badoo.meetingroom.di.modules.GetCredentialModule;
import com.badoo.meetingroom.di.modules.RoomEventModule;
import com.badoo.meetingroom.di.modules.ApplicationModule;
import com.badoo.meetingroom.presentation.view.activity.BaseActivity;
import com.badoo.meetingroom.presentation.view.activity.MainActivity;
import com.badoo.meetingroom.presentation.view.activity.RoomEventsActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, RoomEventModule.class, GetCredentialModule.class})
public interface ApplicationComponent {

  void inject(BaseActivity activity);
  void inject(RoomEventsActivity activity);
  void inject(MainActivity activity);
}
