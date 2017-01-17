package com.badoo.meetingroom.domain.interactor;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.domain.repository.GoogleAccountRepo;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class PutGoogleAccount extends UseCase<Void> {

    public static final String NAME = "putGoogleAccount";

    private final GoogleAccountRepo googleAccountRepository;
    private String accountName;

    @Inject
    PutGoogleAccount(GoogleAccountRepo googleAccountRepository) {
        this.googleAccountRepository = googleAccountRepository;
    }

    public PutGoogleAccount init(@NonNull String accountName) {
        this.accountName = accountName;
        return this;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (this.accountName == null) {
            throw new IllegalArgumentException("init(accountName) not called, or called with null argument");
        }
        return googleAccountRepository.putAccountName(accountName);
    }
}
