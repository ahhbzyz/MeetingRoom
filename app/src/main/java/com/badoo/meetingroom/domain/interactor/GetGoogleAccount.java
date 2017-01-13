package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.entity.intf.GoogleAccount;
import com.badoo.meetingroom.domain.repository.GoogleAccountDataRepo;


import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class GetGoogleAccount extends UseCase<GoogleAccount> {

    public static final String NAME = "getGoogleAccount";

    private final GoogleAccountDataRepo googleAccountRepository;
    private Subscription subscription = Subscriptions.empty();

    @Inject
    GetGoogleAccount(GoogleAccountDataRepo googleAccountRepository) {
        this.googleAccountRepository = googleAccountRepository;
    }

    @Override
    public Observable <GoogleAccount> buildUseCaseObservable() {
        return googleAccountRepository.getAccountName();
    }

    @Override
    public void unSubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
