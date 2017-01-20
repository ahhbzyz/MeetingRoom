package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.model.EventModelImpl;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class CalendarEventsMapper {
    private long mEventStartTime;
    private long mEventEndTime;

    @Inject
    CalendarEventsMapper() {
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

    public List<EventModel> map(List<LocalEvent> roomEventList) {

        if (mEventStartTime > mEventEndTime) {
            throw new IllegalArgumentException("Event end time cannot less than event end time");
        }

        List<EventModel> roomEventModelList = new ArrayList<>();

        if (roomEventList != null) {

            long firstEventStartTime = roomEventList.isEmpty() ? mEventStartTime : roomEventList.get(0).getStartTime();
            long startTime, endTime;
            long lastEventEndTime = endTime = firstEventStartTime < mEventStartTime ? firstEventStartTime : mEventStartTime;

            for (LocalEvent roomEvent : roomEventList) {

                final EventModel roomEventModel = map(roomEvent);

                if (roomEventModel != null) {

                    // Skip event start at same time
                    if (roomEvent.getStartTime() < endTime) {
                        continue;
                    }

                    startTime = roomEvent.getStartTime();
                    endTime = roomEvent.getEndTime();


                    // CHeck here
                    if (startTime > lastEventEndTime) {
                        roomEventModelList.addAll(generateAvailableEvents(lastEventEndTime, startTime));
                    }

                    addTimeStampsBetweenPeriod(roomEventModel);
                    roomEventModelList.add(roomEventModel);
                    lastEventEndTime = endTime;
                }
            }

            if (lastEventEndTime < mEventEndTime) {
                roomEventModelList.addAll(generateAvailableEvents(lastEventEndTime, mEventEndTime));
                roomEventModelList.get(roomEventModelList.size() - 1).getTimeStamps().add(mEventEndTime);
            } else {
                roomEventModelList.get(roomEventModelList.size() - 1).getTimeStamps().add(lastEventEndTime);
            }
        }


        return roomEventModelList;
    }


    private List<EventModel> generateAvailableEvents(long startTime, long endTime) {

        List<EventModel> eventList = new ArrayList<>();

        long timestamp =  TimeHelper.dropMinutes(startTime);

        while (true) {

            timestamp += TimeHelper.min2Millis(15);

            if (timestamp > startTime && timestamp < endTime) {
                eventList.add(generateAvailableEvent(startTime, timestamp));
                startTime = timestamp;
            }
            if (timestamp > endTime) {
                eventList.add(generateAvailableEvent(startTime, endTime));
                break;
            }
        }

        return eventList;
    }

    private void addTimeStampsBetweenPeriod(EventModel eventModel) {

        List<Long> result = new ArrayList<>();

        long timestamp = TimeHelper.dropMinutes(eventModel.getStartTime());

        while (true) {

            if (timestamp >= eventModel.getStartTime() && timestamp < eventModel.getEndTime()) {
                result.add(timestamp);
            }
            if (timestamp > eventModel.getEndTime()) {
                break;
            }


            timestamp += TimeHelper.min2Millis(15);
        }

        eventModel.setTimeStamps(result);
    }

    private EventModel generateAvailableEvent(long startTime, long endTime) {
        EventModel roomEvent = new EventModelImpl();
        roomEvent.setStartTime(startTime);
        roomEvent.setEndTime(endTime);
        roomEvent.setStatus(LocalEventImpl.AVAILABLE);
        addTimeStampsBetweenPeriod(roomEvent);
        return roomEvent;
    }

    public void setEventStartTime(long eventStartTime) {
        if (eventStartTime < TimeHelper.getMidNightTimeOfDay(0)) {
            throw new IllegalArgumentException("Event start time cannot less than today");
        }
        mEventStartTime = eventStartTime;
    }

    public void setEventEndTime(long eventEndTime) {
        if (eventEndTime < TimeHelper.getMidNightTimeOfDay(0)) {
            throw new IllegalArgumentException("Event end time cannot less than today");
        }
        mEventEndTime = eventEndTime;
    }
}
