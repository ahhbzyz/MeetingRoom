package com.badoo.meetingroom.presentation.presenter.intf;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.RoomEventsView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface RoomEventsPresenter extends Presenter {
    void setView(@NonNull RoomEventsView roomEventsView);
    void onCountDownTicking(long millisUntilFinished);
    void onCountDownFinished();
    void updateCurrentTimeForHtv();

    RoomEventModel getCurrentEvent();
    void confirmEvent();
    void dismissEvent();
    void setDoNotDisturb();
}
