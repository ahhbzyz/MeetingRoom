package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventModelMapper {

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
}
