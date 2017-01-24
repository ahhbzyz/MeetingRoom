package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.impl.LocalEventImpl;
import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.impl.EventModelImpl;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 18/01/2017.
 */

public class EventModelMapper {
    private long mEventStartTime;
    private long mEventEndTime;

    @Inject
    EventModelMapper() {
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

    public List<EventModel> map(List<LocalEvent> eventModelList) {

        if (mEventStartTime > mEventEndTime) {
            throw new IllegalArgumentException("Event end time cannot less than event end time");
        }

        List<EventModel> eventModelModelList = new ArrayList<>();

        if (eventModelList != null) {

            long firstEventStartTime = eventModelList.isEmpty() ? mEventStartTime : eventModelList.get(0).getStartTime();
            long startTime, endTime;
            long lastEventEndTime = endTime = firstEventStartTime < mEventStartTime ? firstEventStartTime : mEventStartTime;

            for (LocalEvent eventModel : eventModelList) {

                final EventModel eventModelModel = map(eventModel);

                if (eventModelModel != null) {

                    // Skip event start at same time
                    if (eventModel.getStartTime() < endTime) {
                        continue;
                    }

                    startTime = eventModel.getStartTime();
                    endTime = eventModel.getEndTime();


                    // CHeck here
                    if (startTime > lastEventEndTime) {
                        eventModelModelList.addAll(generateAvailableEvents(lastEventEndTime, startTime));
                    }

                    addTimeStampsBetweenPeriod(eventModelModel);
                    eventModelModelList.add(eventModelModel);
                    lastEventEndTime = endTime;
                }
            }

            if (lastEventEndTime < mEventEndTime) {
                eventModelModelList.addAll(generateAvailableEvents(lastEventEndTime, mEventEndTime));
                eventModelModelList.get(eventModelModelList.size() - 1).getTimeStamps().add(mEventEndTime);
            } else {
                eventModelModelList.get(eventModelModelList.size() - 1).getTimeStamps().add(lastEventEndTime);
            }
        }


        return eventModelModelList;
    }


    private List<EventModel> generateAvailableEvents(long startTime, long endTime) {

        List<EventModel> eventList = new ArrayList<>();

        long timestamp =  TimeHelper.dropMinutes(startTime);

        while (true) {

            timestamp += TimeHelper.min2Millis(15);

            if (timestamp > startTime && timestamp < endTime) {
                eventList.add(generateAvailableEvent(startTime, timestamp, endTime));
                startTime = timestamp;
            }
            if (timestamp > endTime) {
                eventList.add(generateAvailableEvent(startTime, endTime, endTime));
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

    private EventModel generateAvailableEvent(long startTime, long endTime, long nextBusyEventStartTime) {
        EventModel eventModel = new EventModelImpl();
        eventModel.setStartTime(startTime);
        eventModel.setEndTime(endTime);
        eventModel.setNextBusyEventStartTime(nextBusyEventStartTime);
        eventModel.setStatus(LocalEventImpl.AVAILABLE);
        addTimeStampsBetweenPeriod(eventModel);
        return eventModel;
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
