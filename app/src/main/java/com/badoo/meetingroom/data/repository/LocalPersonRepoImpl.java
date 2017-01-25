package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.impl.PersonStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonStore;
import com.badoo.meetingroom.domain.entity.intf.LocalPerson;
import com.badoo.meetingroom.domain.mapper.LocalPersonMapper;
import com.badoo.meetingroom.domain.repository.LocalPersonRepo;

import java.util.List;


import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

@Singleton
public class LocalPersonRepoImpl implements LocalPersonRepo {

    private LocalPersonMapper mBadooPersonDataMapper;
    private final PersonStore mPersonDataStore;

    @Inject
    LocalPersonRepoImpl(LocalPersonMapper badooPersonDataMapper, PersonStoreFactory personDataStoreFactory) {
        mBadooPersonDataMapper = badooPersonDataMapper;
        mPersonDataStore = personDataStoreFactory.createRemotePersonStore();
    }

    @Override
    public Observable<List<LocalPerson>> getBadooPersonList() {
        return mPersonDataStore.getPersonList().map(this.mBadooPersonDataMapper::map);
    }

    @Override
    public Observable<LocalPerson> getBadooPerson(String personId) {
        return mPersonDataStore.getPerson(personId).map(this.mBadooPersonDataMapper::map);
    }

}
