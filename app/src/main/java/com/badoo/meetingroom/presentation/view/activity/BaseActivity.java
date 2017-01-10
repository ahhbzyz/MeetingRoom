package com.badoo.meetingroom.presentation.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.di.components.ApplicationComponent;
import com.badoo.meetingroom.di.components.DaggerMeetingRoomBookingComponent;
import com.badoo.meetingroom.di.components.MeetingRoomBookingComponent;
import com.badoo.meetingroom.di.modules.ActivityModule;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);
    }

    protected MeetingRoomBookingComponent getComponent() {
        return DaggerMeetingRoomBookingComponent
            .builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(getActivityModule())
            .build();
    }

    private ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    private ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }
}
