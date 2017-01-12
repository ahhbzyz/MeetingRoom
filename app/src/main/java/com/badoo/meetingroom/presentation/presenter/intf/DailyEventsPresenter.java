package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.view.view.DailyEventsView;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsPresenter extends Presenter {

    void updateCurrentTimeLayout();

    void setView(DailyEventsView dailyEventsView);

    void onEventClicked(int position);

    void getEvents();
}
