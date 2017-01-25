package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.domain.entity.intf.LocalPerson;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public interface LocalPersonRepo {
    Observable<List<LocalPerson>> getBadooPersonList();
    Observable<LocalPerson> getBadooPerson(String personId);
}
