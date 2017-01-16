package com.badoo.meetingroom.domain.repository;

import com.bumptech.glide.request.FutureTarget;

import java.io.File;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public interface RemoteImageRepo {
    Observable<FutureTarget<File>> getImage(String url, int width, int height);
}
