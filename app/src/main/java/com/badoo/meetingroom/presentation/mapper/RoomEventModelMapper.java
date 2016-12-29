package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.domain.entity.RoomEventImpl;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventModelMapper {

    private long mEventStartTime;
    private long mEventEndTime;

    @Inject
    RoomEventModelMapper() {
        // Default time period is one day
        mEventStartTime = TimeHelper.getMidNightTimeOfDay(0);
        mEventEndTime = TimeHelper.getMidNightTimeOfDay(1);
    }

    private RoomEventModel map(RoomEvent roomEvent) {
        if (roomEvent == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }

        final RoomEventModel roomEventModel = new RoomEventModel();
        roomEventModel.setOrganizer(roomEvent.getOrganizer());
        roomEventModel.setStatus(roomEvent.getStatus());
        roomEventModel.setStartTime(roomEvent.getStartTime());
        roomEventModel.setEndTime(roomEvent.getEndTime());
        roomEventModel.setStatus(RoomEventImpl.BUSY);
        return roomEventModel;
    }

    public List<RoomEventModel> map(List<RoomEvent> roomEventList) {

        if (mEventStartTime > mEventEndTime) {
            throw new IllegalArgumentException("Event end time cannot less than event end time");
        }

        List<RoomEventModel> roomEventModelList = new ArrayList<>();

        if (roomEventList != null) {

            long startTime = mEventStartTime;

            long endTime = roomEventList.isEmpty() ? mEventEndTime : roomEventList.get(0).getStartTime();

            if (endTime > mEventEndTime || endTime < mEventStartTime) {
                return  roomEventModelList;
            }


            RoomEventModel firstEvent = generateAvailableEvent(startTime, endTime);
            roomEventModelList.add(firstEvent);

            long lastEventEndTime = roomEventModelList.get(0).getEndTime();

            for (RoomEvent roomEvent : roomEventList) {
                final RoomEventModel roomEventModel = map(roomEvent);

                if (roomEventModel != null) {
                    startTime = roomEvent.getStartTime();
                    endTime = roomEvent.getEndTime();

                    if (startTime < mEventStartTime || startTime > mEventEndTime ||
                        endTime > mEventEndTime || endTime < mEventStartTime) {
                        return roomEventModelList;
                    }

                    if (startTime > lastEventEndTime) {
                        final RoomEventModel availableEvent = generateAvailableEvent(lastEventEndTime, startTime);
                        roomEventModelList.add(availableEvent);
                    }

                    roomEventModelList.add(roomEventModel);
                    lastEventEndTime = endTime;
                }
            }

            if (lastEventEndTime < mEventEndTime) {
                RoomEventModel lastEvent = generateAvailableEvent(lastEventEndTime, mEventEndTime);
                roomEventModelList.add(lastEvent);
            }
        }

        return roomEventModelList;
    }

    private RoomEventModel generateAvailableEvent(long startTime, long endTime) {
        RoomEventModel roomEvent = new RoomEventModel();
        roomEvent.setStartTime(startTime);
        roomEvent.setEndTime(endTime);
        roomEvent.setOrganizer("None");
        roomEvent.setStatus(RoomEventImpl.AVAILABLE);
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

