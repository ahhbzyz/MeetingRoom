package com.badoo.meetingroom.domain.interactor;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.domain.repository.GoogleAccountRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class WriteGoogleAccount extends UseCase2<Void, String> {

    public static final String NAME = "writeGoogleAccount";

    private final GoogleAccountRepository googleAccountRepository;
    private String accountName;

    @Inject
    public WriteGoogleAccount(GoogleAccountRepository googleAccountRepository) {
        this.googleAccountRepository = googleAccountRepository;
    }

    public WriteGoogleAccount init(@NonNull String accountName) {
        this.accountName = accountName;
        return this;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (this.accountName == null) {
            throw new IllegalArgumentException("init(accountName) not called, or called with null argument");
        }
        return Observable.concat(validate(), googleAccountRepository.writeAccountName(accountName));
    }

    private Observable<Void> validate() {
        return Observable.create(subscriber -> {
            if (WriteGoogleAccount.this.accountName.isEmpty()) {

            } else {
                subscriber.onCompleted();
            }
        });
    }
}
