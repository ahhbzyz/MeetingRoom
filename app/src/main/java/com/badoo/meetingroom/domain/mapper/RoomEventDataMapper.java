package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.entity.RoomEventImpl;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

@Singleton
public class RoomEventDataMapper {

    @Inject
    public RoomEventDataMapper() {

    }

    private RoomEvent generateAvailableEvent(long startTime, long endTime) {
        RoomEvent roomEvent = new RoomEventImpl();
        roomEvent.setStartTime(startTime);
        roomEvent.setEndTime(endTime);
        roomEvent.setStatus(RoomEventImpl.AVAILABLE);
        return roomEvent;
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

    public List<RoomEvent> transform(List<Event> eventList) {
        final List<RoomEvent> roomEventList = new ArrayList<>();

        long startTime = getMidNightTimeOfNextDays(0);
        long endTime = eventList.isEmpty() ?
                       getMidNightTimeOfNextDays(1) :
                       eventList.get(0).getStart().getDateTime().getValue();

        RoomEvent firstEvent = generateAvailableEvent(startTime, endTime);
        roomEventList.add(firstEvent);
        long lastEventEndTime = roomEventList.get(0).getEndTime();

        for (Event event : eventList) {

            final RoomEvent roomEvent = transform(event);

            if (roomEvent != null) {

                startTime = roomEvent.getStartTime();
                endTime = roomEvent.getEndTime();

                if (startTime > lastEventEndTime) {
                    final RoomEvent availableEvent = generateAvailableEvent(lastEventEndTime, startTime);
                    roomEventList.add(availableEvent);
                }

                roomEventList.add(roomEvent);
                lastEventEndTime = endTime;
            }
        }

        if (lastEventEndTime < getMidNightTimeOfNextDays(1)) {
            RoomEvent lastEvent = generateAvailableEvent(lastEventEndTime, getMidNightTimeOfNextDays(1));
            roomEventList.add(lastEvent);
        }

        return roomEventList;
    }

    private long getMidNightTimeOfNextDays(int i) {

        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, i);
        return date.getTimeInMillis();
    }
}
