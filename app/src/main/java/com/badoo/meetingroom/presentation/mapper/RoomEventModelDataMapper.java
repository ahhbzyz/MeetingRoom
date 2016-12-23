package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventModelDataMapper {

    @Inject
    public RoomEventModelDataMapper() {}

    private RoomEventModel transform(RoomEvent roomEvent) {
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

    public Collection<RoomEventModel> transform(Collection<RoomEvent> roomEventCollection) {
        Collection<RoomEventModel> roomEventModelCollection;

        if (roomEventCollection != null && !roomEventCollection.isEmpty()) {

            roomEventModelCollection = new ArrayList<>();

            for(RoomEvent roomEvent : roomEventCollection) {
                roomEventModelCollection.add(transform(roomEvent));
            }
        } else {
            roomEventModelCollection = Collections.emptyList();
        }

        return roomEventModelCollection;
    }
}
