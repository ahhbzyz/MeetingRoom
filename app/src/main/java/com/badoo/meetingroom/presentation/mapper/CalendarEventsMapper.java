package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.model.EventModelImpl;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    private EventModel map(LocalEvent roomEvent) {
        if (roomEvent == null) {
            throw new IllegalArgumentException("Cannot map a null value");
        }

        final EventModel roomEventModel = new EventModelImpl();

        roomEventModel.setId(roomEvent.getId());
        roomEventModel.setCreatorId(roomEvent.getCreatorId());
        roomEventModel.setCreatorEmailAddress(roomEvent.getCreatorEmailAddress());
        roomEventModel.setStatus(roomEvent.getStatus());
        roomEventModel.setStartTime(roomEvent.getStartTime());
        roomEventModel.setEndTime(roomEvent.getEndTime());
        roomEventModel.setStatus(EventModel.BUSY);

        if (roomEvent.isFastBook()) {
            roomEventModel.setConfirmed(true);
        }
        return roomEventModel;
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

                    roomEventModelList.add(roomEventModel);
                    lastEventEndTime = endTime;
                }
            }

            if (lastEventEndTime < mEventEndTime) {
                roomEventModelList.addAll(generateAvailableEvents(lastEventEndTime, mEventEndTime));
            }
        }

        return roomEventModelList;
    }


    private List<EventModel> generateAvailableEvents(long startTime, long endTime) {

        List<EventModel> eventList = new ArrayList<>();

        EventModel topMarginEvent = new EventModelImpl();
        eventList.add(topMarginEvent);


        float hour = TimeHelper.getHour(startTime);
        while (true) {
            long timestamp = mEventStartTime + TimeHelper.min2Millis((int) (hour * 60));
            hour += 0.5f;
            if (timestamp > startTime && timestamp < endTime) {
                eventList.add(generateAvailableEvent(startTime, timestamp));
                startTime = timestamp;
            }
            if (timestamp > endTime) {
                eventList.add(generateAvailableEvent(startTime, endTime));
                break;
            }
        }

        EventModel bottomMarginEvent = new EventModelImpl();
        eventList.add(bottomMarginEvent);

        return eventList;
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
