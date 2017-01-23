package com.badoo.meetingroom.presentation.view.activity;

import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.view.component.button.LongPressButton;
import com.badoo.meetingroom.presentation.view.component.button.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

class RoomStatusHandler {

    private final RoomStatusActivity activity;
    private boolean hasRequested;

    @Inject
    RoomStatusHandler(RoomStatusActivity activity) {
        this.activity = activity;
    }

    void updateRoomStatusView(EventModel event) {
        if (event == null) {
            return;
        }

        activity.mCircleView.setColorTheme(
            event.getEventColor(),
            event.getEventBgColor()
        );

        // Reset circle background paint style
        activity.mCircleView.setCircleBackgroundPaintStyle(Paint.Style.STROKE);


        switch (event.getStatus()) {

            case EventModel.AVAILABLE:

                showAvailableView(event);

                break;

            case EventModel.BUSY:

                if (event.isOnHold()) {

                    showOnHoldView();

                } else if (event.isDoNotDisturb()) {

                    showDoNotDisturbView(event);

                } else {

                    showBusyView(event);

                }

                break;
            default:
                break;
        }
    }

    void showAvailableView(EventModel currentEvent) {

        // Text
        activity.mRoomStatusTv.setText(activity.getString(R.string.available_for_upper_case));
        long hours = TimeUnit.MILLISECONDS.toHours(currentEvent.getRemainingTimeUntilNextBusyEvent());
        if (hours >= 2) {
            setTimerText(activity.getString(R.string.two_hour_plus));
        }

        // Visibilities
        activity.mCircleView.setTailIconVisibility(false);
        activity.mFastBookTv.setVisibility(View.VISIBLE);
        activity.mCircleTimerInfoLayout.setVisibility(View.VISIBLE);
        activity.mCircleTimeViewBtn.setVisibility(View.VISIBLE);
        activity.mDndLayout.setVisibility(View.INVISIBLE);

        // Update button group layout
        activity.mButtonsLayout.removeAllViews();

        // Circle time view button
        activity.mCircleTimeViewBtn.setImageDrawable(activity.getDrawable(R.drawable.ic_add_small));
        activity.mCircleTimeViewBtn.setBackground(activity.getDrawable(R.drawable.btn_circle_available));

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

    void showOnHoldView() {

        // Text view
        activity.mRoomStatusTv.setText(activity.getString(R.string.on_hold_for_upper_case));

        // Visibilities
        activity.mCircleView.setTailIconVisibility(true);
        activity.mFastBookTv.setVisibility(View.GONE);
        activity.mCircleTimerInfoLayout.setVisibility(View.VISIBLE);
        activity.mCircleTimeViewBtn.setVisibility(View.INVISIBLE);
        activity.mDndLayout.setVisibility(View.INVISIBLE);

        // Remove buttons
        activity.mButtonsLayout.removeAllViews();

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_onhold, null);
        activity.mButtonsLayout.addView(btnGroup);

        TextView confirmTv = (TextView) btnGroup.findViewById(R.id.tv_confirm);
        confirmTv.setTypeface(activity.mStolzlRegularTypeface);

        TextView dismissTv = (TextView) btnGroup.findViewById(R.id.tv_dismiss);
        dismissTv.setTypeface(activity.mStolzlRegularTypeface);

        ImageButton confirmBtn = (ImageButton) btnGroup.findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(v -> activity.mPresenter.setEventConfirmed());


        ImageButton dismissBtn = (ImageButton) btnGroup.findViewById(R.id.btn_dismiss);
        dismissBtn.setOnClickListener(v -> activity.mPresenter.deleteEvent());
    }

    void showBusyView(EventModel currentEvent) {

        // Text view
        activity.mRoomStatusTv.setText(activity.getString(R.string.busy_until_upper_case));
        setTimerText(currentEvent.getEndTimeInText());

        // Visibilities
        activity.mCircleView.setTailIconVisibility(true);
        activity.mFastBookTv.setVisibility(View.GONE);
        activity.mCircleTimerInfoLayout.setVisibility(View.VISIBLE);
        activity.mCircleTimeViewBtn.setVisibility(View.VISIBLE);
        activity.mDndLayout.setVisibility(View.INVISIBLE);

        activity.mButtonsLayout.removeAllViews();

        activity.mCircleTimeViewBtn.setImageDrawable(activity.getDrawable(R.drawable.ic_info));
        activity.mCircleTimeViewBtn.setBackground(activity.getDrawable(R.drawable.btn_circle_busy));

        activity.mCircleTimeViewBtn.setOnClickListener(v -> activity.mPresenter.onCircleTimeViewBtnClick());

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_busy, null);
        activity.mButtonsLayout.addView(btnGroup);


        TextView dndTv = (TextView) btnGroup.findViewById(R.id.tv_dnd);
        dndTv.setTypeface(activity.mStolzlRegularTypeface);

        TextView endTv = (TextView) btnGroup.findViewById(R.id.tv_hold_to_end);
        endTv.setTypeface(activity.mStolzlRegularTypeface);

        TextView extendTv = (TextView) btnGroup.findViewById(R.id.tv_extend);
        extendTv.setTypeface(activity.mStolzlRegularTypeface);

        LongPressButton endBtn = (LongPressButton) btnGroup.findViewById(R.id.btn_end);
        endBtn.setOnCountDownListener(() -> activity.mPresenter.deleteEvent());

        ImageButton dndBtn = (ImageButton) btnGroup.findViewById(R.id.btn_dnd);
        dndBtn.setOnClickListener(v -> activity.mPresenter.setDoNotDisturb(true));

        ImageButton extendBtn = (ImageButton) btnGroup.findViewById(R.id.btn_extend);
        extendBtn.setOnClickListener(v -> activity.mPresenter.updateEvent());
    }

    void showDoNotDisturbView(EventModel currentEvent) {

        activity.mCircleView.setColorTheme(currentEvent.getEventColor(), currentEvent.getEventColor());
        activity.mCircleView.setCircleBackgroundPaintStyle(Paint.Style.FILL_AND_STROKE);

        // Visibilities
        activity.mCircleView.setTailIconVisibility(false);
        activity.mFastBookTv.setVisibility(View.GONE);
        activity.mCircleTimerInfoLayout.setVisibility(View.INVISIBLE);
        activity.mCircleTimeViewBtn.setVisibility(View.INVISIBLE);
        activity.mDndLayout.setVisibility(View.VISIBLE);

        activity.mButtonsLayout.removeAllViews();

        activity.mCircleView.setOnClickListener(v -> activity.mPresenter.setDoNotDisturb(false));

        View btnGroup = View.inflate(activity.getApplicationContext(), R.layout.layout_btn_group_dnd, null);
        activity.mButtonsLayout.addView(btnGroup);

        TextView busyUntilTv = (TextView) btnGroup.findViewById(R.id.tv_busy_until);
        busyUntilTv.setTypeface(activity.mStolzlRegularTypeface);

        TextView endTimeTv = (TextView) btnGroup.findViewById(R.id.tv_end_time);
        endTimeTv.setTypeface(activity.mStolzlMediumTypeface);
        endTimeTv.setText(currentEvent.getEndTimeInText());
    }

    void updateRoomStatusTextView(EventModel mCurrentEvent) {
        switch (mCurrentEvent.getStatus()) {
            case EventModel.AVAILABLE:
                long hours = TimeUnit.MILLISECONDS.toHours(mCurrentEvent.getRemainingTimeUntilNextBusyEvent());
                if (hours < 2) {
                    setTimerText(TimeHelper.formatMillisInMinAndSec(mCurrentEvent.getRemainingTimeUntilNextBusyEvent()));
                }
                break;
            case EventModel.BUSY:
                if (mCurrentEvent.isOnHold() && !mCurrentEvent.isConfirmed()) {
                    setTimerText(TimeHelper.formatMillisInMinAndSec(mCurrentEvent.getRemainingOnHoldTime()));
                }
                break;
            default:
                break;
        }
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

    void updateExtendButtonState(boolean state) {
        ImageButton extendBtn = (ImageButton) activity.mButtonsLayout.findViewById(R.id.btn_extend);
        if (extendBtn != null) {
            if (state) {
                extendBtn.setAlpha(1f);
                extendBtn.setEnabled(true);
            } else {
                extendBtn.setEnabled(false);
                extendBtn.setAlpha(.3f);
            }
        }
    }
}
