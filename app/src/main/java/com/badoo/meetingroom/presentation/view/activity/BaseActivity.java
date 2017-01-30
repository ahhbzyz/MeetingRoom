package com.badoo.meetingroom.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.di.components.ApplicationComponent;
import com.badoo.meetingroom.di.components.DaggerMeetingRoomBookingComponent;
import com.badoo.meetingroom.di.components.MeetingRoomBookingComponent;
import com.badoo.meetingroom.di.modules.ActivityModule;
import com.badoo.meetingroom.presentation.CalendarUpdateService;

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
        onSystemTimeRefresh();
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

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                onSystemTimeRefresh();
            }
        }
    };

    private BroadcastReceiver mCalendarUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onCalendarUpdate();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mCalendarUpdateReceiver, new IntentFilter(CalendarUpdateService.TAG));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mTimeRefreshReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCalendarUpdateReceiver);
    }

    protected abstract void onSystemTimeRefresh();

    protected void onCalendarUpdate() {
    }

}
