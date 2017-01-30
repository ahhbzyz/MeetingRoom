package com.badoo.meetingroom.domain.interactor.googleaccount;

import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepo;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class PutGoogleAccount extends UseCase {

    public static final String NAME = "putGoogleAccount";

    private final GoogleAccountRepo mGoogleAccountRepo;

    @Inject
    PutGoogleAccount(GoogleAccountRepo googleAccountRepo) {
        mGoogleAccountRepo = googleAccountRepo;
    }

    private Observable<Void> buildUseCaseObservable(String accountName) {
        if (accountName == null) {
            throw new IllegalArgumentException("init(accountName) not called, or called with null argument");
        }
        return mGoogleAccountRepo.putAccountName(accountName);
    }

    public void execute(Subscriber<Void> useCaseSubscriber, String accountName) {
        mSubscription = buildUseCaseObservable(accountName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
