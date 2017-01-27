package com.badoo.meetingroom.domain.mapper;

import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.badoo.meetingroom.presentation.Badoo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class LocalEventMapper {

    @Inject
    GoogleAccountCredential mCredential;

    @Inject
    LocalEventMapper() {}

    private LocalEvent map(Event event) {

        LocalEvent localEvent = null;

        if (event != null && event.getStart().getDateTime() != null
                          && event.getEnd().getDateTime() != null) {

            String creatorEmailAddress = null;
            String creatorId = null;
            String creatorName = null;

            if (event.getCreator() != null ) {
                if (event.getCreator().getEmail().equals(mCredential.getSelectedAccountName())) {
                    if (event.getAttendees() != null && !event.getAttendees().isEmpty()) {
                        creatorId = event.getAttendees().get(0).getId();
                        creatorEmailAddress = event.getAttendees().get(0).getEmail();
                        creatorName = event.getAttendees().get(0).getDisplayName();

                    }
                }else{
                    creatorId = event.getCreator().getId();
                    creatorEmailAddress = event.getCreator().getEmail();
                    creatorName = event.getCreator().getDisplayName();
                }
            }

            long startTime = event.getStart().getDateTime().getValue();
            long endTime = event.getEnd().getDateTime().getValue();

            localEvent = new LocalEventImpl();
            localEvent.setId(event.getId());
            localEvent.setCreatorId(creatorId);
            localEvent.setCreatorName(creatorName);
            localEvent.setCreatorEmailAddress(creatorEmailAddress);
            localEvent.setStartTime(startTime);
            localEvent.setEndTime(endTime);
            localEvent.setEventTitle(event.getSummary());
            localEvent.setStatus(LocalEvent.BUSY);

            if (event.getAttendees() != null) {
                for (EventAttendee eventAttendee : event.getAttendees()) {
                    if (eventAttendee.getEmail().equals(mCredential.getSelectedAccountName()) &&
                        eventAttendee.getResponseStatus().equals("declined")) {
                        return null;
                    }
                }
            }


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
