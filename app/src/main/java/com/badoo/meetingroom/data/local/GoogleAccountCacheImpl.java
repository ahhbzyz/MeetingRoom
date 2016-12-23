package com.badoo.meetingroom.data.local;

import android.content.Context;

import com.badoo.meetingroom.data.exception.GooglePlayServicesAvailabilityException;
import com.badoo.meetingroom.data.exception.NoAccountNameFoundInCacheException;
import com.badoo.meetingroom.data.exception.NoPermissionToAccessContacts;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

@Singleton
public class GoogleAccountCacheImpl implements GoogleAccountCache {

    private final Context mContext;
    private final FileManager mFileManager;
    private final GoogleServiceConnector mConnector;

    private String PREF_FILE_NAME = "GoogleAccount";
    private String PREF_ACCOUNT_NAME = "accountName";


    @Inject
    public GoogleAccountCacheImpl(Context context, FileManager fileManager, GoogleServiceConnector googleServiceConnector) {
        if (context == null || fileManager == null || googleServiceConnector == null) {
            throw new IllegalArgumentException("Invalid null parameter");
        }
        this.mContext = context;
        this.mFileManager = fileManager;
        this.mConnector = googleServiceConnector;
    }

    @Override
    public Observable<String> get() {
        return Observable.create(subscriber -> {
            if (!mConnector.isGooglePlayServicesAvailable()) {
                if (!mConnector.acquireGooglePlayServices()) {
                    subscriber.onError(new GooglePlayServicesAvailabilityException(mConnector.getGooglePlayServicesResult()));
                } else {
                    get();
                }
            } else {
                if (mConnector.hasPermissionToAccessContacts()) {
                    String accountName = mFileManager.getAccountNameFromPreferences(mContext, PREF_FILE_NAME, PREF_ACCOUNT_NAME);
                    if (accountName == null) {
                        subscriber.onError(new NoAccountNameFoundInCacheException());
                    } else {
                        subscriber.onNext(accountName);
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onError(new NoPermissionToAccessContacts());
                }
            }
        });
    }

    @Override
    public Observable<Void> write(String accountName) {
        return Observable.create(subscriber -> {
            if(accountName != null) {
                mFileManager.writeAccountNameToPreferences(mContext, PREF_FILE_NAME, PREF_ACCOUNT_NAME, accountName);
                subscriber.onCompleted();
            }
        });
    }
}
