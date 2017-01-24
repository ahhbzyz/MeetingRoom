package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomStatusView extends LoadDataView {

    void renderRoomEvent(EventModel nextEvent);

    void renderRoomEventList(List<EventModel> roomEvents);

    void updateRoomStatusView(EventModel currentEvent);

    void updateHorizontalTimeline();

    void bookRoomFrom(int position, ArrayList<EventModel> eventModelList);

    void startCircleViewAnimator(EventModel currentEvent);

    void showEventOrganizerDialog(EventModel mCurrentEvent);

    void hideTopBottomContent();

    void showTopBottomContent();

    void stopCountDown();

    void updateExtendButtonState(boolean state);

    void updateRoomStatusTextView(EventModel mCurrentEvent);

}
