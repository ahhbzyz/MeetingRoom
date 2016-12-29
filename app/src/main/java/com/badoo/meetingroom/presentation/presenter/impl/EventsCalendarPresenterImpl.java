package com.badoo.meetingroom.presentation.presenter.impl;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.presenter.intf.EventsCalendarPresenter;
import com.badoo.meetingroom.presentation.view.EventsCalendarView;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public class EventsCalendarPresenterImpl implements EventsCalendarPresenter {

    private EventsCalendarView mEventsCalendarView;

    @Inject
    EventsCalendarPresenterImpl() {

    }

    @Override
    public void init() {
        setUpToolbar();
        setUpViewPager();
    }


    private void setUpToolbar() {
        mEventsCalendarView.setUpToolbar();
        loadAvailableRooms();
    }

    private void loadAvailableRooms() {
    }

    private void setUpViewPager() {
        mEventsCalendarView.setUpViewPager();
    }



    @Override
    public void setView(@NonNull EventsCalendarView eventsCalendarView) {
        this.mEventsCalendarView = eventsCalendarView;
    }

    @Override
    public void updateFragmentCurrTimeMark() {
        mEventsCalendarView.updateFragmentCurrTimeMark();
    }

    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {

    }
}
