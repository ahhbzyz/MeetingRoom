package com.badoo.meetingroom.data;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.model.Event.Organizer;
import com.google.api.services.calendar.model.EventDateTime;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class InsertEventParams {
    private GoogleAccountCredential credential;
    private String organizer;
    private EventDateTime startDataTime;
    private EventDateTime endDataTime;

    private InsertEventParams() {}

    public GoogleAccountCredential getCredential() {
        return credential;
    }

    public String getOrganizer(){
        return organizer;
    }

    public EventDateTime getStartDataTime() {
        return startDataTime;
    }

    public EventDateTime getEndDataTime() {
        return endDataTime;
    }

    public static class EventParamsBuilder {

        private final GoogleAccountCredential credential;
        private String organizer;
        private EventDateTime startDateTime;
        private EventDateTime endDateTime;

        public EventParamsBuilder(GoogleAccountCredential credential) {
            this.credential = credential;
        }

        public EventParamsBuilder organizer(String organizer) {
            this.organizer = organizer;
            return this;
        }

        public EventParamsBuilder startDateTime(EventDateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public EventParamsBuilder endDateTime(EventDateTime endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }

        private void validateEventsParamsObject() {
            if (credential == null) {
                throw new IllegalArgumentException("Invalid null parameter");
            }
        }

        public InsertEventParams build() {
            InsertEventParams insertEventParams = new InsertEventParams();
            insertEventParams.credential = credential;
            insertEventParams.organizer = organizer;
            insertEventParams.startDataTime = startDateTime;
            insertEventParams.endDataTime = endDateTime;
            validateEventsParamsObject();
            return insertEventParams;
        }
    }
}
