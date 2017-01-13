package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;

import com.badoo.meetingroom.data.local.FileManager;
import com.badoo.meetingroom.data.local.GoogleAccountCache;
import com.badoo.meetingroom.data.local.GoogleAccountCacheImpl;
import com.badoo.meetingroom.data.local.GoogleServicesConnector;
import com.badoo.meetingroom.data.repository.datasource.intf.GoogleAccountDataStore;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class GoogleAccountDataStoreFactory {

    private final Context mContext;
    private final FileManager mFileManager;
    private final GoogleServicesConnector mConnector;

    @Inject
    public GoogleAccountDataStoreFactory(Context context, FileManager fileManager, GoogleServicesConnector connector) {
        this.mContext = context.getApplicationContext();
        this.mFileManager = fileManager;
        this.mConnector = connector;
    }

    public GoogleAccountDataStore createLocalGoogleAccountNameStore() {
        GoogleAccountCache googleAccountCache = new GoogleAccountCacheImpl(this.mContext, mFileManager, mConnector);
        return new GoogleAccountDataStoreImpl(googleAccountCache);
    }
}
