package com.badoo.meetingroom.data.remote.googleglideapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.badoo.meetingroom.data.exception.NetworkConnectionException;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class GoogleGlideApiImpl implements GoogleGlideApi{

    private Context mContext;

    public GoogleGlideApiImpl(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The constructor parameters cannot be null");
        }
        this.mContext = context;
    }


    @Override
    public Observable<FutureTarget<File>> getImage(String url, int width, int height) {
        return Observable.create(subscriber -> {
            if(hasInternetConnection()) {
                try {
                    FutureTarget<File> result = getImageFromApi(mContext, url, width, height);
                    if (result != null) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }


    private FutureTarget<File> getImageFromApi(Context context, String url, int width, int height) throws Exception {
        return GetImageApiCall.createGET(context, url, width, height).requestSyncCall();
    }

    private boolean hasInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager =
            (ConnectivityManager)this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }


}
