package com.badoo.meetingroom.domain.interactor;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

abstract class UseCaseWithParams<T, P> extends UseCase<T> {

    UseCaseWithParams() {}

    public UseCaseWithParams<T, P> init(P p) {
        return this;
    }
}
