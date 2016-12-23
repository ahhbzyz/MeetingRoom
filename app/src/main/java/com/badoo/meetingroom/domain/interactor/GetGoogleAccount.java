package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.entity.GoogleAccount;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;


import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class GetGoogleAccount extends UseCase<GoogleAccount>{

    private final GoogleAccountRepository googleAccountRepository;
    private Subscription subscription = Subscriptions.empty();

    @Inject
    public GetGoogleAccount(GoogleAccountRepository googleAccountRepository) {
        this.googleAccountRepository = googleAccountRepository;
    }

    @Override
    public Observable <GoogleAccount> buildUseCaseObservable() {
        return googleAccountRepository.getAccountName();
    }

    @Override
    public void execute(Subscriber <GoogleAccount> useCaseSubscriber) {
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
