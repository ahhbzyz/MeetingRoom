package com.badoo.meetingroom.data;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */
public class GetEventsParams {

    private GoogleAccountCredential credential;
    private DateTime startTime;
    private DateTime endTime;

    private GetEventsParams() {}

    public GoogleAccountCredential getCredential() {
        return credential;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public static class EventsParamsBuilder {

        private final GoogleAccountCredential credential;
        private DateTime startTime;
        private DateTime endTime;

        public EventsParamsBuilder(GoogleAccountCredential credential) {
            this.credential = credential;
        }

        public EventsParamsBuilder startTime (DateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public EventsParamsBuilder endTime (DateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        private void validateEventsParamsObject() {
            if (credential == null) {
                throw new IllegalArgumentException("Invalid null parameter");
            }
        }

        public GetEventsParams build() {
            GetEventsParams getEventsParams = new GetEventsParams();
            getEventsParams.credential = credential;
            getEventsParams.startTime = startTime;
            getEventsParams.endTime = endTime;
            validateEventsParamsObject();
            return getEventsParams;
        }

    }
}
