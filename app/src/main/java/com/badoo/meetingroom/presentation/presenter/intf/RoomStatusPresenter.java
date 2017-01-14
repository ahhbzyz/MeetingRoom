package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.view.RoomEventsView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */
public interface RoomStatusPresenter extends Presenter {
    void setView(@NonNull RoomEventsView roomEventsView);

    void onCountDownTicking(long millisUntilFinished);
    void onCountDownFinished();
    void circleTimeViewBtnClick();
    void setEventConfirmed();
    void setDoNotDisturb(boolean isDoNotDisturb);
    void onEventClicked(int position);
    void onRestart();

    void getEvents();
    void deleteEvent();
    void updateEvent();
    void insertEvent(int bookingPeriod);
    void onSystemTimeUpdate();

}
