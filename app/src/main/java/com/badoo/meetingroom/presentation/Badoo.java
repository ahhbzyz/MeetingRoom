package com.badoo.meetingroom.presentation;

import com.badoo.meetingroom.presentation.model.RoomModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

/**
 * Created by zhangyaozhong on 06/01/2017.
 */

public class Badoo {
    private static final int START_TIME = 0;
    private static final int END_TIME = 24;

    private static RoomModel ROOM;

    // default public constructor
    public static RoomModel getCurrentRoom() {
        return ROOM;
    }

    public static void setCurrentRoom(RoomModel roomModel) {
        ROOM = roomModel;
    }

    public static long getStartTimeOfDay(int day) {
        return TimeHelper.getMidNightTimeOfDay(day) + TimeHelper.hr2Millis(START_TIME);
    }

    public static long getEndTimeOfDay(int day) {
        return TimeHelper.getMidNightTimeOfDay(day + 1) - TimeHelper.hr2Millis(24 - END_TIME);
    }
}
