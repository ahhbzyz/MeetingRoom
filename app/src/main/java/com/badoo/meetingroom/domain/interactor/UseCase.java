package com.badoo.meetingroom.domain.interactor;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public abstract class UseCase {

    protected Subscription mSubscription = Subscriptions.empty();

    public void unSubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
