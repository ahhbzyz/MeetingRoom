package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.view.DailyEventsView;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsPresenter extends Presenter {
    void setView(DailyEventsView dailyEventsView);
    void updateCurrentTimeMark();
    float getWidthTimeRatio();
    void updateCurrentTimeMarkWhenScrolled(int dy);
    void onEventClicked(int position);
}
