package com.badoo.meetingroom.domain.excutor;

import rx.Scheduler;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface PostExecutionThread {
    Scheduler getScheduler();
}
