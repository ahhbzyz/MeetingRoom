package com.badoo.meetingroom.domain.interactor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public abstract class UseCase<T> {
    protected Subscription mSubscription = Subscriptions.empty();

    protected abstract Observable<T> buildUseCaseObservable();

    public void unSubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public void execute(Subscriber<T> useCaseSubscriber) {
        this.mSubscription = this.buildUseCaseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
