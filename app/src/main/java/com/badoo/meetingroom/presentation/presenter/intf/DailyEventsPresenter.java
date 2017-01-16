package com.badoo.meetingroom.presentation.presenter.intf;

import android.view.View;

import com.badoo.meetingroom.presentation.view.view.DailyEventsView;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsPresenter extends Presenter {

    void onSystemTimeUpdate();

    void setView(DailyEventsView dailyEventsView);

    void onEventClicked(View view, int position);

    void getEvents();

    void updateCurrentTimeLayoutPosition();
}
