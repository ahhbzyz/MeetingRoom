package com.badoo.meetingroom.data.remote.googleglideapi;

import android.content.Context;

import com.bumptech.glide.request.FutureTarget;


import java.io.File;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public interface GoogleGlideApi {
    Observable<FutureTarget<File>> getImage(String url, int width, int height);
}
