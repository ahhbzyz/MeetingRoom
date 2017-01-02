package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.domain.entity.impl.RoomEventImpl;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class RoomEventDataMapper {

    @Inject
    RoomEventDataMapper() {

    }
    private RoomEvent transform(Event event) {
        RoomEvent roomEvent = null;
        if (event != null && event.getStart().getDateTime() != null
                          && event.getEnd().getDateTime() != null) {

            String organizer = event.getOrganizer().getEmail();
            long startTime = event.getStart().getDateTime().getValue();
            long endTime = event.getEnd().getDateTime().getValue();

            roomEvent = new RoomEventImpl();
            roomEvent.setOrganizer(organizer);
            roomEvent.setStartTime(startTime);
            roomEvent.setEndTime(endTime);
            roomEvent.setStatus(RoomEventImpl.BUSY);
        }
        return roomEvent;
    }

    public List<RoomEvent> map(List<Event> eventList) {
        final List<RoomEvent> roomEventList = new ArrayList<>();

        for (Event event : eventList) {

            final RoomEvent roomEvent = transform(event);

            if (roomEvent != null) {
                roomEventList.add(roomEvent);
            }
        }
        return roomEventList;
    }
}
