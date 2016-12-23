package com.badoo.meetingroom.data.repository.datasource;

import android.content.Context;
import android.support.annotation.NonNull;

import com.badoo.meetingroom.data.local.FileManager;
import com.badoo.meetingroom.data.local.GoogleAccountCache;
import com.badoo.meetingroom.data.local.GoogleAccountCacheImpl;
import com.badoo.meetingroom.data.local.GoogleServiceConnector;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class GoogleAccountDataStoreFactory {

    private final Context mContext;
    private final FileManager mFileManager;
    private final GoogleServiceConnector mConnector;

    @Inject
    public GoogleAccountDataStoreFactory(Context context, FileManager fileManager, GoogleServiceConnector connector) {
        this.mContext = context.getApplicationContext();
        this.mFileManager = fileManager;
        this.mConnector = connector;
    }

    public GoogleAccountDataStore createLocalGoogleAccountNameStore() {
        GoogleAccountCache googleAccountCache = new GoogleAccountCacheImpl(this.mContext, mFileManager, mConnector);
        return new LocalGoogleAccountDataStore(googleAccountCache);
    }
}
