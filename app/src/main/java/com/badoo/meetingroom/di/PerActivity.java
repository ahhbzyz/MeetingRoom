package com.badoo.meetingroom.di;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */

@Scope
@Retention(RUNTIME)
public @interface PerActivity {}