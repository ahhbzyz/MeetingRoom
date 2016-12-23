package com.badoo.meetingroom.di;

import android.app.Application;

import com.badoo.meetingroom.di.components.ApplicationComponent;
import com.badoo.meetingroom.di.components.DaggerApplicationComponent;
import com.badoo.meetingroom.di.modules.ApplicationModule;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class AndroidApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.initInjector();
    }

    private void initInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

}
