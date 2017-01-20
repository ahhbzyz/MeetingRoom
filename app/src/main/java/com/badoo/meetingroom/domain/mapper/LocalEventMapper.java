package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.badoo.meetingroom.presentation.Badoo;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class LocalEventMapper {

    @Inject
    LocalEventMapper() {}

    private LocalEvent map(Event event) {

        LocalEvent localEvent = null;

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

            localEvent = new LocalEventImpl();
            localEvent.setId(event.getId());
            localEvent.setCreatorId(creatorId);
            localEvent.setCreatorName(event.getCreator().getDisplayName());
            localEvent.setCreatorEmailAddress(creatorEmailAddress);
            localEvent.setStartTime(startTime);
            localEvent.setEndTime(endTime);
            localEvent.setEventTitle(event.getSummary());
            localEvent.setStatus(LocalEvent.BUSY);

            if (event.getDescription() != null && event.getDescription().equals(LocalEvent.FAST_BOOKING_DESCRIPTION)) {
                localEvent.setFastBooking(true);
            }
        }
        return localEvent;
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
