package com.badoo.meetingroom.data.repository;

import com.badoo.meetingroom.data.repository.datasource.impl.PersonDataStoreFactory;
import com.badoo.meetingroom.data.repository.datasource.intf.PersonDataStore;
import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.badoo.meetingroom.domain.mapper.BadooPersonDataMapper;
import com.badoo.meetingroom.domain.repository.BadooPersonDataRepo;

import java.util.List;


import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

@Singleton
public class BadooPersonDataRepoImpl implements BadooPersonDataRepo{

    private BadooPersonDataMapper mBadooPersonDataMapper;
    private final PersonDataStore mPersonDataStore;

    @Inject
    BadooPersonDataRepoImpl(BadooPersonDataMapper badooPersonDataMapper, PersonDataStoreFactory personDataStoreFactory) {
        this.mBadooPersonDataMapper = badooPersonDataMapper;
        this.mPersonDataStore = personDataStoreFactory.createRemotePersonDataStore();
    }

    @Override
    public Observable<List<BadooPerson>> getBadooPersonList() {
        return mPersonDataStore.getPersonList().map(this.mBadooPersonDataMapper::map);
    }

}
