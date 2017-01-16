package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.remote.googleglideapi.GoogleGlideApi;
import com.badoo.meetingroom.data.repository.datasource.intf.RemoteImageStore;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

class RemoteImageStoreImpl implements RemoteImageStore {

    private final GoogleGlideApi mGoogleApi;

    RemoteImageStoreImpl(GoogleGlideApi googleApi) {
        this.mGoogleApi = googleApi;
    }

    @Override
    public Observable<FutureTarget<File>> getImage(String url, int width, int height) {
        return mGoogleApi.getImage(url, width, height);
    }
}
