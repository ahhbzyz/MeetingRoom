package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.view.EventsCalendarView;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface EventsCalendarPresenter extends Presenter {
    void setView(@NonNull EventsCalendarView eventsCalendarView);
}
