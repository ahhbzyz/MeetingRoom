package com.badoo.meetingroom.di.components;

import android.content.Context;

import com.badoo.meetingroom.di.modules.ApplicationModule;
import com.badoo.meetingroom.domain.repository.LocalPersonRepo;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepo;
import com.badoo.meetingroom.domain.repository.CalendarApiRepo;
import com.badoo.meetingroom.presentation.view.activity.BaseActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.Calendar;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})

public interface ApplicationComponent {
  void inject(BaseActivity activity);

  Context context();
  GoogleAccountCredential googleAccountCredential();
  Calendar services();
  CalendarApiRepo roomEventRepository();
  LocalPersonRepo badooPersonRepository();
  GoogleAccountRepo googleAccountRepository();
}
