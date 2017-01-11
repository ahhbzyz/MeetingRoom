package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.view.view.DailyEventsView;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsPresenter extends Presenter {
    void setView(DailyEventsView dailyEventsView);
    void init();
    void onEventClicked(int position);
    float getNumOfExpiredEvents();
    void loadRoomEventList();
    void updateNumOfExpiredEvents();
}
