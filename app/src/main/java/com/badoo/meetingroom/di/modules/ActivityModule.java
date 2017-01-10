package com.badoo.meetingroom.di.modules;

import android.app.Activity;

import com.badoo.meetingroom.di.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity Activity activity() {
        return activity;
    }
}
