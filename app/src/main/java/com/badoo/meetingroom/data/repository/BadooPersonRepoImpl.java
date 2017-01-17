package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.impl.PersonStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonStore;
import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.badoo.meetingroom.domain.mapper.BadooPersonMapper;
import com.badoo.meetingroom.domain.repository.BadooPersonRepo;

import java.util.List;


import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

@Singleton
public class BadooPersonRepoImpl implements BadooPersonRepo {

    private BadooPersonMapper mBadooPersonDataMapper;
    private final PersonStore mPersonDataStore;

    @Inject
    BadooPersonRepoImpl(BadooPersonMapper badooPersonDataMapper, PersonStoreFactory personDataStoreFactory) {
        this.mBadooPersonDataMapper = badooPersonDataMapper;
        this.mPersonDataStore = personDataStoreFactory.createRemotePersonStore();
    }

    @Override
    public Observable<List<BadooPerson>> getBadooPersonList() {
        return mPersonDataStore.getPersonList().map(this.mBadooPersonDataMapper::map);
    }

    @Override
    public Observable<BadooPerson> getBadooPerson(String personId) {
        return mPersonDataStore.getPerson(personId).map(this.mBadooPersonDataMapper::map);
    }

}
