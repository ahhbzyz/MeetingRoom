package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class LocalEventMapper {

    @Inject
    LocalEventMapper() {}

    private LocalEvent map(Event event) {

        LocalEvent roomEvent = null;

        if (event != null && event.getStart().getDateTime() != null
                          && event.getEnd().getDateTime() != null) {

            String creatorEmailAddress = null;
            String creatorId = null;

            if (event.getCreator().isSelf()) {
                if (event.getAttendees() != null && !event.getAttendees().isEmpty()) {
                    creatorId = event.getAttendees().get(0).getId();
                    creatorEmailAddress = event.getAttendees().get(0).getEmail();
                }
            } else {
                creatorId = event.getCreator().getId();
                creatorEmailAddress = event.getCreator().getEmail();
            }

            long startTime = event.getStart().getDateTime().getValue();
            long endTime = event.getEnd().getDateTime().getValue();

            roomEvent = new LocalEventImpl();
            roomEvent.setId(event.getId());
            roomEvent.setCreatorId(creatorId);
            roomEvent.setCreatorEmailAddress(creatorEmailAddress);
            roomEvent.setStartTime(startTime);
            roomEvent.setEndTime(endTime);
            roomEvent.setStatus(LocalEvent.BUSY);

            if (event.getDescription() != null && event.getDescription().equals("fast_book")) {
                roomEvent.setFastBook(true);
            }
        }
        return roomEvent;
    }

    public List<LocalEvent> map(List<Event> eventList) {
        final List<LocalEvent> roomEventList = new ArrayList<>();

        for (Event event : eventList) {

            final LocalEvent roomEvent = map(event);

            if (roomEvent != null) {
                roomEventList.add(roomEvent);
            }
        }
        return roomEventList;
    }
}
