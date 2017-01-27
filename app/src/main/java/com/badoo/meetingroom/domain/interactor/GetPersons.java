package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.repository.LocalPersonRepo;
import com.badoo.meetingroom.presentation.mapper.PersonModelMapper;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class GetPersons extends UseCase {

    public static final String NAME = "getPersons";

    private final LocalPersonRepo mBadooPersonDataRepo;
    private final PersonModelMapper mPersonModelMapper;

    @Inject
    GetPersons(LocalPersonRepo badooPersonDataRepo, PersonModelMapper personModelMapper) {
        this.mBadooPersonDataRepo = badooPersonDataRepo;
        this.mPersonModelMapper = personModelMapper;
    }

    private Observable<List<PersonModel>> buildUseCaseObservable() {
        return mBadooPersonDataRepo.getBadooPersonList().map(mPersonModelMapper::map);
    }

    public void execute(Subscriber<List<PersonModel>> useCaseSubscriber) {
        mSubscription = buildUseCaseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(useCaseSubscriber);
    }
}