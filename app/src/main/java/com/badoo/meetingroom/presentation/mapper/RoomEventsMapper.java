package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.model.EventModelImpl;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventsMapper {

    private long mEventStartTime;
    private long mEventEndTime;

    @Inject
    RoomEventsMapper() {
        // Default time period is one day
        mEventStartTime = TimeHelper.getMidNightTimeOfDay(0);
        mEventEndTime = TimeHelper.getMidNightTimeOfDay(1);
    }

    private EventModel map(LocalEvent localEvent) {
        if (localEvent == null) {
            throw new IllegalArgumentException("Cannot map a null value");
        }

        final EventModel eventModel = new EventModelImpl();

        eventModel.setId(localEvent.getId());
        eventModel.setCreatorId(localEvent.getCreatorId());
        eventModel.setCreatorName(localEvent.getCreatorName());
        eventModel.setCreatorEmailAddress(localEvent.getCreatorEmailAddress());
        eventModel.setStatus(localEvent.getStatus());
        eventModel.setStartTime(localEvent.getStartTime());
        eventModel.setEndTime(localEvent.getEndTime());
        eventModel.setEventTitle(localEvent.getEventTitle());
        eventModel.setFastBooking(localEvent.isFastBooking());
        eventModel.setStatus(EventModel.BUSY);

        if (localEvent.isFastBooking()) {
            eventModel.setConfirmed(true);
        }
        return eventModel;
    }

    public List<EventModel> map(List<LocalEvent> localEventList) {

        if (mEventStartTime > mEventEndTime) {
            throw new IllegalArgumentException("Event end time cannot less than event end time");
        }

        List<EventModel> eventModelList = new ArrayList<>();

        if (localEventList != null) {

            long firstEventStartTime = localEventList.isEmpty() ? mEventStartTime : localEventList.get(0).getStartTime();
            long startTime, endTime;
            long lastEventEndTime = endTime = firstEventStartTime < mEventStartTime ? firstEventStartTime : mEventStartTime;

            for (LocalEvent roomEvent : localEventList) {

                final EventModel roomEventModel = map(roomEvent);

                if (roomEventModel != null) {

                    // Skip event start at same time
                    if (roomEvent.getStartTime() < endTime) {
                        continue;
                    }

                    startTime = roomEvent.getStartTime();
                    endTime = roomEvent.getEndTime();

                    if (startTime > lastEventEndTime) {
                        final EventModel availableEvent = generateAvailableEvent(lastEventEndTime, startTime);
                        eventModelList.add(availableEvent);
                    }

                    eventModelList.add(roomEventModel);
                    lastEventEndTime = endTime;
                }
            }

            if (lastEventEndTime < mEventEndTime) {
                EventModel lastEvent = generateAvailableEvent(lastEventEndTime, mEventEndTime);
                eventModelList.add(lastEvent);
            }
        }

        return eventModelList;
    }

    private EventModel generateAvailableEvent(long startTime, long endTime) {
        EventModel roomEvent = new EventModelImpl();
        roomEvent.setStartTime(startTime);
        roomEvent.setEndTime(endTime);
        roomEvent.setStatus(LocalEventImpl.AVAILABLE);
        return roomEvent;
    }

    public void setEventStartTime(long eventStartTime) {
        if (eventStartTime < TimeHelper.getMidNightTimeOfDay(0)) {
            throw new IllegalArgumentException("Event start time cannot less than today");
        }
        this.mEventStartTime = eventStartTime;
    }

    public void setEventEndTime(long eventEndTime) {
        if (eventEndTime < TimeHelper.getMidNightTimeOfDay(0)) {
            throw new IllegalArgumentException("Event end time cannot less than today");
        }
        this.mEventEndTime = eventEndTime;
    }
}

