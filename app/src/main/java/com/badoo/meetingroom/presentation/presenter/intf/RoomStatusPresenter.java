package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.view.RoomStatusView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */
public interface RoomStatusPresenter extends Presenter {

    void setView(RoomStatusView roomEventsView);

    void onCountDownTicking();
    void onCountDownFinished();
    void onCircleTimeViewBtnClick();
    void setEventConfirmed();
    void setDoNotDisturb(boolean isDoNotDisturb);
    void onRestart();
    void updateHorizontalTimeline();

    void getNumOfAvailableRooms();

    void getEvents();
    void deleteEvent();
    void updateEvent();
    void insertEvent(int bookingPeriod);
    void onSystemTimeRefresh();

}
