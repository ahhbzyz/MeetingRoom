package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.impl.RoomImpl;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class RoomMapper {

    private final Gson mGson;

    @Inject
    RoomMapper() {
        mGson = new Gson();
    }

    private Room map(CalendarListEntry entry) {

        if (entry == null) {
            return null;
        }

        Room room;

        String description = entry.getDescription();

        if (description != null) {

            room = mGson.fromJson(description, RoomImpl.class);

        } else {
            room = new RoomImpl();
            room.setName(entry.getSummary());
            room.setId(entry.getId());
            // tparse json

            Gson gson = new Gson();

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
        }

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
