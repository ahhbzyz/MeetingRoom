package com.badoo.meetingroom.presentation.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

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
        setImmersiveMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            setImmersiveMode();
        }
    }

    protected MeetingRoomBookingComponent getComponent() {
        return DaggerMeetingRoomBookingComponent
            .builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(getActivityModule())
            .build();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setImmersiveMode();
    }

    private ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    private ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }

    public void setImmersiveMode() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
