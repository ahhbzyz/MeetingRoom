package com.badoo.meetingroom.presentation.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.di.components.ApplicationComponent;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }
}
