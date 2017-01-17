package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;

import com.badoo.meetingroom.data.remote.googleglideapi.GoogleGlideApi;
import com.badoo.meetingroom.data.remote.googleglideapi.GoogleGlideApiImpl;
import com.badoo.meetingroom.data.repository.datasource.intf.RemoteImageStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

@Singleton
public class RemoteImageStoreFactory {

    private final Context mContext;

    @Inject
    RemoteImageStoreFactory(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public RemoteImageStore createRemoteImageStore() {
        GoogleGlideApi googleGlideApi = new GoogleGlideApiImpl(mContext);
        return new RemoteImageStoreImpl(googleGlideApi);
    }
}
