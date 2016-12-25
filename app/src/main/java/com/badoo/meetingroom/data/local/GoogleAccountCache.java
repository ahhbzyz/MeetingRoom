package com.badoo.meetingroom.data.local;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public interface GoogleAccountCache {
    Observable<String> get();
    Observable<Void> put(String accountName);
}
