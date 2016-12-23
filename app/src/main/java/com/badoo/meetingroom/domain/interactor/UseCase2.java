package com.badoo.meetingroom.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public abstract class UseCase2<T, P> extends UseCase {

    UseCase2() {}

    public UseCase2<T, P> init(P p) {
        return this;
    }


}
