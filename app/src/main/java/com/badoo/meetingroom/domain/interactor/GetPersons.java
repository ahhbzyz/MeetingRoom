package com.badoo.meetingroom.domain.interactor;

import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.badoo.meetingroom.domain.repository.BadooPersonDataRepo;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class GetPersons extends UseCase<List<BadooPerson>> {

    public static final String NAME = "getPersons";

    private final BadooPersonDataRepo mBadooPersonDataRepo;

    @Inject
    GetPersons(BadooPersonDataRepo badooPersonDataRepo) {
        this.mBadooPersonDataRepo = badooPersonDataRepo;

    }

    @Override
    protected Observable<List<BadooPerson>> buildUseCaseObservable() {
        return mBadooPersonDataRepo.getBadooPersonList();
    }
}