package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.impl.RemoteImageStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.RemoteImageStore;
import com.badoo.meetingroom.domain.repository.RemoteImageRepo;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

@Singleton
public class RemoteImageRepoImpl implements RemoteImageRepo {

    private final RemoteImageStore mRemoteImageStore;

    @Inject
    RemoteImageRepoImpl(RemoteImageStoreFactory remoteImageStoreFactory) {
        this.mRemoteImageStore = remoteImageStoreFactory.createRemoteImageStore();
    }

    @Override
    public Observable<FutureTarget<File>> getImage(String url, int width, int height) {
        return mRemoteImageStore.getImage(url, width, height);
    }
}
