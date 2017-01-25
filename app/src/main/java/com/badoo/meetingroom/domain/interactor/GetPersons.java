package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.repository.LocalPersonRepo;
import com.badoo.meetingroom.presentation.mapper.PersonModelMapper;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class GetPersons extends UseCase<List<PersonModel>> {

    public static final String NAME = "getPersons";

    private final LocalPersonRepo mBadooPersonDataRepo;
    private final PersonModelMapper mPersonModelMapper;

    @Inject
    GetPersons(LocalPersonRepo badooPersonDataRepo, PersonModelMapper personModelMapper) {
        this.mBadooPersonDataRepo = badooPersonDataRepo;
        this.mPersonModelMapper = personModelMapper;
    }

    @Override
    protected Observable<List<PersonModel>> buildUseCaseObservable() {
        return mBadooPersonDataRepo.getBadooPersonList().map(mPersonModelMapper::map);
    }
}