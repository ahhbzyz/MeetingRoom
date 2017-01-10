package com.badoo.meetingroom.di.components;

import android.app.Activity;

import com.badoo.meetingroom.di.PerActivity;
import com.badoo.meetingroom.di.modules.ActivityModule;

import dagger.Component;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
interface ActivityComponent {
    Activity activity();
}
