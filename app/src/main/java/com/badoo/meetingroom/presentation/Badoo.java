package com.badoo.meetingroom.presentation;

import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

/**
 * Created by zhangyaozhong on 06/01/2017.
 */

public class Badoo {
    public static final long START_TIME = TimeHelper.getMidNightTimeOfDay(0) + TimeHelper.hr2Millis(8);
    public static final long END_TIME = TimeHelper.getMidNightTimeOfDay(1) - TimeHelper.hr2Millis(4);
}
