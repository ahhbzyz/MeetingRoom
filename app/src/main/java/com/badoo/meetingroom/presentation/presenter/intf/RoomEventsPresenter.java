package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.RoomEventsView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface RoomEventsPresenter extends Presenter {
    void setView(@NonNull RoomEventsView roomEventsView);
    void onCountDownTicking();
    void onCountDownFinished();
}