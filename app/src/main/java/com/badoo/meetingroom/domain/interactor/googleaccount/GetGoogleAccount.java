package com.badoo.meetingroom.domain.interactor.googleaccount;

import com.badoo.meetingroom.domain.interactor.UseCase;
import com.badoo.meetingroom.domain.repository.GoogleAccountRepo;
import com.badoo.meetingroom.presentation.mapper.GoogleAccountModelMapper;
import com.badoo.meetingroom.presentation.model.impl.GoogleAccountModel;


import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class GetGoogleAccount extends UseCase {

    public static final String NAME = "getGoogleAccount";

    private final GoogleAccountRepo mGoogleAccountRepository;
    private final GoogleAccountModelMapper mGoogleAccountModelMapper;

    @Inject
    GetGoogleAccount(GoogleAccountRepo googleAccountRepository, GoogleAccountModelMapper googleAccountModelMapper) {
        mGoogleAccountRepository = googleAccountRepository;
        mGoogleAccountModelMapper = googleAccountModelMapper;
    }

    private Observable <GoogleAccountModel> buildUseCaseObservable() {
        return mGoogleAccountRepository.getAccountName().map(mGoogleAccountModelMapper::map);
    }

    public void execute(Subscriber<GoogleAccountModel> useCaseSubscriber) {
        mSubscription = buildUseCaseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}
