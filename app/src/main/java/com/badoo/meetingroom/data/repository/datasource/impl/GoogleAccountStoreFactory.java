package com.badoo.meetingroom.data.repository.datasource.impl;

import android.content.Context;

import com.badoo.meetingroom.data.local.FileManager;
import com.badoo.meetingroom.data.local.GoogleAccountCache;
import com.badoo.meetingroom.data.local.GoogleAccountCacheImpl;
import com.badoo.meetingroom.data.local.GoogleServicesConnector;
import com.badoo.meetingroom.data.repository.datasource.intf.GoogleAccountStore;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class GoogleAccountStoreFactory {

    private final Context mContext;
    private final FileManager mFileManager;
    private final GoogleServicesConnector mConnector;

    @Inject
    GoogleAccountStoreFactory(Context context, FileManager fileManager, GoogleServicesConnector connector) {
        mContext = context.getApplicationContext();
        mFileManager = fileManager;
        mConnector = connector;
    }

    public GoogleAccountStore createGoogleAccountNameStore() {
        GoogleAccountCache googleAccountCache = new GoogleAccountCacheImpl(mContext, mFileManager, mConnector);
        return new GoogleAccountStoreImpl(googleAccountCache);
    }
}
