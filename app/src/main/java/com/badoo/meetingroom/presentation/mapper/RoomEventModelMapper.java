package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.entity.RoomEventImpl;
import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventModelMapper {

    private long startDateTime;

    @Inject
    RoomEventModelMapper() {}

    private RoomEventModel map(RoomEvent roomEvent) {
        if (roomEvent == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }

        final RoomEventModel roomEventModel = new RoomEventModel();
        roomEventModel.setOrganizer(roomEvent.getOrganizer());
        roomEventModel.setStatus(roomEvent.getStatus());
        roomEventModel.setStartTime(roomEvent.getStartTime());
        roomEventModel.setEndTime(roomEvent.getEndTime());
        return roomEventModel;
    }

    public List<RoomEventModel> map(Collection<RoomEvent> roomEventCollection) {
        List<RoomEventModel> roomEventModelList;

        if (roomEventCollection != null && !roomEventCollection.isEmpty()) {

            roomEventModelList = new ArrayList<>();

            for(RoomEvent roomEvent : roomEventCollection) {
                roomEventModelList.add(map(roomEvent));
            }
        } else {
            roomEventModelList = Collections.emptyList();
        }

        return roomEventModelList;
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

    private int daysBetween(long t1, long t2) {
        return (int) ((t2 - t1) / (1000 * 60 * 60 * 24));
    }

    private RoomEvent generateAvailableEvent(long startTime, long endTime) {
        RoomEvent roomEvent = new RoomEventImpl();
        roomEvent.setStartTime(startTime);
        roomEvent.setEndTime(endTime);
        roomEvent.setStatus(RoomEventImpl.AVAILABLE);
        return roomEvent;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }
}
//
//    final List<RoomEvent> roomEventList = new ArrayList<>();
//
//    long eventStartTime = eventList.isEmpty() ?  getMidNightTimeOfNextDays(0) : eventList.get(0).getStart().getDateTime().getValue();
//
//    long startTime = getMidNightTimeOfNextDays(daysBetween(getMidNightTimeOfNextDays(0),  eventStartTime));
//    long endTime = eventList.isEmpty() ? getMidNightTimeOfNextDays(1) : eventStartTime;
//
//    RoomEvent firstEvent = generateAvailableEvent(startTime, endTime);
//roomEventList.add(firstEvent);
//    long lastEventEndTime = roomEventList.get(0).getEndTime();
//
//    for (Event event : eventList) {
//
//final RoomEvent roomEvent = transform(event);
//
//    if (roomEvent != null) {
//
//    startTime = roomEvent.getStartTime();
//    endTime = roomEvent.getEndTime();
//
//    if (startTime > lastEventEndTime) {
//final RoomEvent availableEvent = generateAvailableEvent(lastEventEndTime, startTime);
//    roomEventList.add(availableEvent);
//    }
//
//    roomEventList.add(roomEvent);
//    lastEventEndTime = endTime;
//    }
//    }
//
//    if (lastEventEndTime < getMidNightTimeOfNextDays(1)) {
//    RoomEvent lastEvent = generateAvailableEvent(lastEventEndTime, getMidNightTimeOfNextDays(1));
//    roomEventList.add(lastEvent);
//    }
