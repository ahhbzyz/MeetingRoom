package com.badoo.meetingroom.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by zhangyaozhong on 21/12/2016.
 */

@Singleton
public class FileManager {

    @Inject
    public FileManager () {}

    void putAccountNameToPreferences(Context context, String fileName, String key, String accountName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, accountName);
        editor.apply();
    }

    String getAccountNameFromPreferences(Context context, String fileName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
