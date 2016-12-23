package com.badoo.meetingroom.domain.interactor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public abstract class UseCase<T> {

    private Subscription mSubscription = Subscriptions.empty();

    protected UseCase() {}

    protected abstract Observable<T> buildUseCaseObservable();

    public void execute(Subscriber<T> useCaseSubscriber) {
        this.mSubscription = this.buildUseCaseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }

    public void unSubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
