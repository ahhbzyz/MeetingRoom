package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.EventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsView extends LoadDataView{

    void renderDailyEvents(List<EventModel> roomEventModelList);

    int getCurrentPage();

    void updateRecyclerView();

    void handlerUserRecoverableAuth(UserRecoverableAuthIOException e);
}
