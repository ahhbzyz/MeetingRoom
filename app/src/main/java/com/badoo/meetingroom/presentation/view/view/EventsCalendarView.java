package com.badoo.meetingroom.presentation.view.view;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface EventsCalendarView extends LoadDataView {
    void setUpToolbar();
    void setUpViewPager();
    void updateFragmentCurrTimeMark();
}