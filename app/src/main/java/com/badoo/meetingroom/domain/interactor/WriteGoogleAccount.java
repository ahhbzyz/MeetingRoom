package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class WriteGoogleAccount extends UseCase<Void> {

    private final GoogleAccountRepository googleAccountRepository;
    private final String accountName;
    private Subscription subscription = Subscriptions.empty();

    public WriteGoogleAccount(GoogleAccountRepository googleAccountRepository, String accountName) {
        this.googleAccountRepository = googleAccountRepository;
        this.accountName = accountName;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        return googleAccountRepository.writeAccountName(accountName);
    }

    @Override
    public void execute(Subscriber<Void> useCaseSubscriber) {
        this.subscription = this.buildUseCaseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }

    @Override
    public void unSubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

}
