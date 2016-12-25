package com.badoo.meetingroom.presentation.view.activity;

import android.content.Context;
import android.os.Bundle;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.RoomEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleTimerView;
import com.badoo.meetingroom.presentation.view.component.horizontaltimelineview.HorizontalTimelineView;

import java.util.LinkedList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventsActivity extends BaseActivity implements RoomEventsView {

    @Inject
    RoomEventsPresenterImpl mRoomEventsPresenter;

    @BindView(R.id.ctv_status) CircleTimerView mCtv;
    @BindView(R.id.htv_room_events) HorizontalTimelineView mHtv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_events);
        ButterKnife.bind(this);

        this.getApplicationComponent().inject(this);
        mRoomEventsPresenter.setView(this);
        mRoomEventsPresenter.init();


    }

    @Override
    public void showLoadingData(boolean visibility) {

    }

    @Override
    public void showRetryLoading(boolean visibility) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return this.getApplicationContext();
    }

    @Override
    public void renderRoomEvents(LinkedList<RoomEventModel> roomEventQueue) {
        mCtv.setTailIconDrawable(R.drawable.ic_arrow_left);
        mCtv.setCircleBtnIconDrawable(R.drawable.ic_add_black);
        mCtv.setAlertIconDrawable(R.drawable.ic_alert_white);
        mCtv.setOnCountDownListener(mOnCountDownListener);
    }

    private CircleTimerView.OnCountDownListener mOnCountDownListener =
        new CircleTimerView.OnCountDownListener() {
            @Override
            public void onCountDownTicking(long millisUntilFinished) {
                mRoomEventsPresenter.onCountDownTicking();
            }

            @Override
            public void onCountDownFinished() {
                mRoomEventsPresenter.onCountDownFinished();
            }
        };
}
