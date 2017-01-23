package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.impl.RoomImpl;
import com.badoo.meetingroom.domain.entity.intf.Room;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.ArrayList;
import java.util.List;

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
