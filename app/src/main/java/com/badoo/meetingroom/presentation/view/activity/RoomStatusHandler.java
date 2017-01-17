package com.badoo.meetingroom.presentation.view.activity;

import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.component.button.LongPressButton;
import com.badoo.meetingroom.presentation.view.component.button.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public class RoomStatusHandler {

    private final RoomStatusActivity activity;
    private boolean hasRequested;
    private CountDownTimer mCountDownTimer;

    @Inject
    RoomStatusHandler(RoomStatusActivity activity) {
        this.activity = activity;
    }

    void showButtonGroupForAvailableStatus() {

        activity.mFastBookTv.setVisibility(View.VISIBLE);

        // Update button group layout
        activity.mButtonsLayout.removeAllViews();

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_available, null);
        activity.mButtonsLayout.addView(btnGroup);

        TwoLineTextButton fiveMinBtn = (TwoLineTextButton) btnGroup.findViewById(R.id.btn_five_min);
        TwoLineTextButton tenMinBtn = (TwoLineTextButton) btnGroup.findViewById(R.id.btn_ten_min);
        TwoLineTextButton fifteenMinBtn = (TwoLineTextButton) btnGroup.findViewById(R.id.btn_fifteen_min);

        TwoLineTextButton[] buttons = new TwoLineTextButton[3];
        buttons[0] = fiveMinBtn;
        buttons[1] = tenMinBtn;
        buttons[2] = fifteenMinBtn;

        for (int i = 0; i < buttons.length; i++) {
            final int curr = i;
            buttons[i].setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        for (int j = 0; j < buttons.length; j++) {
                            if (curr != j) {
                                buttons[j].setEnabled(false);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        for (int j = 0; j < buttons.length; j++) {
                            if (curr != j) {
                                buttons[j].setEnabled(true);
                            }
                        }
                        break;
                }
                return false;
            });
            buttons[i].setOnClickListener(v -> {
                if (hasRequested) { return; }
                hasRequested = true;
                activity.mPresenter.insertEvent(5 * (curr + 1));
                Handler handler = new Handler();
                handler.postDelayed(() -> hasRequested = false, 1000);
            });
        }
    }


    void showButtonGroupForOnHoldStatus() {

        activity.mFastBookTv.setVisibility(View.GONE);

        activity.mButtonsLayout.removeAllViews();

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_busy, null);
        activity.mButtonsLayout.addView(btnGroup);

        ImageButton confirmBtn = (ImageButton) btnGroup.findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(v -> activity.mPresenter.setEventConfirmed());


        ImageButton dismissBtn = (ImageButton) btnGroup.findViewById(R.id.btn_dismiss);
        dismissBtn.setOnClickListener(v -> activity.mPresenter.deleteEvent());
    }

    void showButtonGroupForBusyStatus() {

        activity.mFastBookTv.setVisibility(View.GONE);

        activity.mButtonsLayout.removeAllViews();

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_busy, null);
        activity.mButtonsLayout.addView(btnGroup);

        LongPressButton endBtn = (LongPressButton) btnGroup.findViewById(R.id.btn_end);
        endBtn.setOnCountDownListener(() -> activity.mPresenter.deleteEvent());


        ImageButton dndBtn = (ImageButton) btnGroup.findViewById(R.id.btn_dnd);
        dndBtn.setOnClickListener(v -> activity.mPresenter.setDoNotDisturb(true));

        ImageButton extendBtn = (ImageButton) btnGroup.findViewById(R.id.btn_extend);
        extendBtn.setOnClickListener(v -> activity.mPresenter.updateEvent());
    }

    void showButtonGroupForDoNotDisturbStatus(String eventEndTime) {

        activity.mFastBookTv.setVisibility(View.GONE);

        activity.mButtonsLayout.removeAllViews();

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_dnd, null);
        activity.mButtonsLayout.addView(btnGroup);

        TextView busyUntilTv = (TextView) btnGroup.findViewById(R.id.tv_busy_until);
        busyUntilTv.setTypeface(activity.mStolzlRegularTypeface);

        TextView endTimeTv = (TextView) btnGroup.findViewById(R.id.tv_end_time);
        endTimeTv.setTypeface(activity.mStolzlMediumTypeface);
        endTimeTv.setText(eventEndTime);
    }

    void updateCircleTimeViewStatus(RoomEventModel event) {
        if (event == null) {
            return;
        }
        activity.mCircleView.setTailIconVisibility(!event.isAvailable() && !event.isDoNotDisturb());
        activity.mCircleTimerInfoLayout.setVisibility(event.isDoNotDisturb() ? View.INVISIBLE : View.VISIBLE);
        activity.mDndLayout.setVisibility(event.isDoNotDisturb() ? View.VISIBLE : View.INVISIBLE);
        activity.mCircleView.setColorTheme(
            event.getEventColor(),
            event.getEventBgColor()
        );
        activity.mCircleView.setCircleBackgroundPaintStyle(Paint.Style.STROKE);

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        switch (event.getStatus()) {

            case RoomEventModel.AVAILABLE:
                activity.mRoomStatusTv.setText(activity.getString(R.string.available_for_upper_case));

                long hours = TimeUnit.MILLISECONDS.toHours(event.getRemainingTime());
                if (hours >= 2) {
                    setTimerText(activity.getString(R.string.two_hour_plus));
                } else {
                    startCountDownTime(event.getRemainingTime(), 1000);
                }
                break;
            case RoomEventModel.BUSY:

                if (event.isOnHold() && !event.isConfirmed()) {
                    activity.mRoomStatusTv.setText(activity.getString(R.string.on_hold_for_upper_case));
                    startCountDownTime(event.getRemainingOnHoldTime(), 1000);
                } else if (event.isDoNotDisturb()) {
                    activity.mCircleView.setColorTheme(event.getEventColor(), event.getEventColor());
                    activity.mCircleView.setCircleBackgroundPaintStyle(Paint.Style.FILL_AND_STROKE);
                } else {
                    setTimerText(event.getEndTimeInText());
                    activity.mRoomStatusTv.setText(activity.getString(R.string.busy_until_upper_case));
                }
                break;
            default:
                break;
        }
    }

    void stopTextViewsCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    void startCountDownTime(long millisInFuture, long countDownInterval) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                setTimerText(TimeHelper.formatMillisInMinAndSec(millisUntilFinished));
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void setTimerText(String text) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) activity.mTimerColonTv.getLayoutParams();
        if (text.length() == 5) {
            params.weight = 1;
            activity.mTimerColonTv.setLayoutParams(params);
            activity.mTimerColonTv.setText(":");
            activity.mTimerOneTv.setText(String.valueOf(text.charAt(0)));
            activity.mTimerTwoTv.setText(String.valueOf(text.charAt(1)));
            activity.mTimerThreeTv.setText(String.valueOf(text.charAt(3)));
            activity.mTimerFourTv.setText(String.valueOf(text.charAt(4)));
        } else if (text.length() == 3) {
            params.weight = 0;
            activity.mTimerColonTv.setLayoutParams(params);
            activity.mTimerColonTv.setText(text);
            activity.mTimerOneTv.setText(null);
            activity.mTimerTwoTv.setText(null);
            activity.mTimerThreeTv.setText(null);
            activity.mTimerFourTv.setText(null);
        }
    }
}
