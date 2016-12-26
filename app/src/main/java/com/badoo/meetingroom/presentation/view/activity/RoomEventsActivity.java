package com.badoo.meetingroom.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.RoomEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleTimerView;
import com.badoo.meetingroom.presentation.view.component.custombutton.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.component.horizontaltimelineview.HorizontalTimelineView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
    @BindView(R.id.layout_btns) LinearLayout mButtonsLayout;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_events);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Loading Data...");

        this.getApplicationComponent().inject(this);
        mRoomEventsPresenter.setView(this);
        mRoomEventsPresenter.init();



        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void showLoadingData(boolean visibility) {
        if (visibility) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
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
    public void setCircleTimeViewTime(long millis) {
        mCtv.setTimerTimeText(millis);
    }

    @Override
    public void setUpCircleTimeView() {
        mCtv.setTailIconDrawable(R.drawable.ic_arrow_left);
        mCtv.setCircleBtnIconDrawable(R.drawable.ic_add_black);
        mCtv.setAlertIconDrawable(R.drawable.ic_alert_white);
        mCtv.setOnCountDownListener(mOnCountDownListener);
    }

    @Override
    public void renderNextRoomEvent(RoomEventModel nextEvent) {
        mCtv.startCountDownTimer(nextEvent);
    }

    @Override
    public void setUpHorizontalTimelineView() {

    }

    @Override
    public void renderRoomEvents(LinkedList<RoomEventModel> mEventModelList) {
        mHtv.setEventList(mEventModelList);
    }

    @Override
    public void setCurrentTime(String currentTime){
        if (currentTime != null) {
            mHtv.setCurrTimeText(currentTime);
        }
    }

    @Override
    public void clearAllButtonsInLayout() {
        if (mButtonsLayout != null) {
            mButtonsLayout.removeAllViews();
        }
    }

    @Override
    public void showButtonsInAvailableStatus() {
        TwoLineTextButton[] buttons = new TwoLineTextButton[3];
        final int min = 5;
        for (int i = 0; i < 3; i++) {
            buttons[i] = new TwoLineTextButton(this, null);
            buttons[i].setTopText(min * (i+1) + "");
            buttons[i].setBottomText("min");
            mButtonsLayout.addView(buttons[i]);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 16, 0);
        buttons[1].setLayoutParams(params);
    }

    @Override
    public void showButtonsInOnHoldStatus() {

    }

    @Override
    public void showButtonsInBusyStatus() {

    }

    @Override
    public void showButtonsInDoNotDisturbStatus() {

    }

    private CircleTimerView.OnCountDownListener mOnCountDownListener =
        new CircleTimerView.OnCountDownListener() {
            @Override
            public void onCountDownTicking(long millisUntilFinished) {
                mRoomEventsPresenter.onCountDownTicking(millisUntilFinished);
            }

            @Override
            public void onCountDownFinished() {
                mRoomEventsPresenter.onCountDownFinished();
            }
        };


    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mRoomEventsPresenter.updateCurrentTimeForHtv();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeRefreshReceiver);
    }
}
