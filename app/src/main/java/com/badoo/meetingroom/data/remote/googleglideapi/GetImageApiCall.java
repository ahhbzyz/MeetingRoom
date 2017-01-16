package com.badoo.meetingroom.data.remote.googleglideapi;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class GetImageApiCall implements Callable<FutureTarget<File>> {

    private Context mContext;
    private String mUrl;
    private int mWidth;
    private int mHeight;

    private GetImageApiCall(Context context, String url, int width, int height) {
        mContext = context;
        mUrl = url;
        mWidth = width;
        mHeight = height;
    }

    static GetImageApiCall createGET(Context context, String url, int width, int height) {
        return new GetImageApiCall(context, url, width, height);
    }

    FutureTarget<File> requestSyncCall() throws Exception {
        return connectToApi();
    }

    private FutureTarget<File> connectToApi() throws Exception {
        return Glide.with(mContext)
            .load(mUrl)
            .downloadOnly(mWidth, mHeight);
    }

    @Override
    public FutureTarget<File> call() throws Exception {
        return requestSyncCall();
    }
}
