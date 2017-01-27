//package com.badoo.meetingroom.domain.interactor;
//
//import com.badoo.meetingroom.domain.entity.intf.LocalPerson;
//import com.badoo.meetingroom.domain.repository.LocalPersonRepo;
//
//import javax.inject.Inject;
//
//import rx.Observable;
//
///**
// * Created by zhangyaozhong on 13/01/2017.
// */
//
//public class GetPerson extends UseCase {
//
//    public static final String NAME = "getPerson";
//    private String mPersonId;
//    private final LocalPersonRepo mBadooPersonDataRepo;
//
//    public GetPerson init(String personId) {
//
//        this.mPersonId = personId;
//        return this;
//    }
//
//    @Inject
//    GetPerson(LocalPersonRepo badooPersonDataRepo) {
//        this.mBadooPersonDataRepo = badooPersonDataRepo;
//    }
//
//    @Override
//    protected Observable<LocalPerson> buildUseCaseObservable() {
//        if (mPersonId == null) {
//            throw new IllegalArgumentException("init(PersonId) not called, or called with null argument");
//        }
//
//        return mBadooPersonDataRepo.getBadooPerson(mPersonId);
//    }
//}