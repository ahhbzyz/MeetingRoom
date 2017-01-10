package com.badoo.meetingroom.di.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */
@Module
public class DialogModule {
    public DialogModule() {}

    @Provides
    ProgressDialog provivdeProgressDialog(Context context) {
        return new ProgressDialog(context);
    }

    @Provides
    Handler provideProgressDialogHandler() {
        return new Handler();
    }

    @Provides
    AlertDialog provideAlertDialog() {

    }
}
