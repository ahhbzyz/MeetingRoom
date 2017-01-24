package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.impl.RoomImpl;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class RoomMapper {

    @Inject
    RoomMapper() {}

    private Room map(CalendarListEntry entry) {

        Room room;

        if (entry == null) {
            return null;
        }
        room = new RoomImpl();
        room.setName(entry.getSummary());
        room.setId(entry.getId());
        String description = entry.getDescription();
        // tparse json
        room.setFloor(1);
        Random rand = new Random();
        int randomNum = rand.nextInt(8 - 1 + 1) + 1;
        boolean flag = randomNum % 2 == 0;
        room.setStationerySupported(flag);

        room.setCapacity(randomNum);

        randomNum = rand.nextInt(2 - 1 + 1) + 1;
        flag = randomNum % 2 == 0;
        room.setBeverageAllowed(flag);

        randomNum = rand.nextInt(3 - 1 + 1) + 1;
        flag = randomNum % 2 == 0;
        room.setTvSupported(flag);

        randomNum = rand.nextInt(7 - 1 + 1) + 1;
        flag = randomNum % 2 == 0;
        room.setVideoConferenceSupported(flag);

        return room;
    }


    public List<Room> map(List<CalendarListEntry> calendarListEntryList) {
        final List<Room> roomList = new ArrayList<>();

        for (CalendarListEntry entry : calendarListEntryList) {
            final Room room = map(entry);
            if (room != null) {
                roomList.add(room);
            }
        }
        return roomList;
    }
}
