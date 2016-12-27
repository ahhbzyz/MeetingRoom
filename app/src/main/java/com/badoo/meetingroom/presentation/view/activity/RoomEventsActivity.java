package com.badoo.meetingroom.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.RoomEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleTimerView;
import com.badoo.meetingroom.presentation.view.component.custombutton.LongPressButton;
import com.badoo.meetingroom.presentation.view.component.custombutton.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.component.horizontaltimelineview.HorizontalTimelineView;
import com.badoo.meetingroom.presentation.view.viewutils.ViewHelper;

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
    @BindView(R.id.layout_btns) LinearLayout mButtonsLayout;
    @BindView(R.id.tv_fast_book) TextView mFastBookTv;

    private ProgressDialog mProgressDialog;


    private TextView mEventEndTimeTv;

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

        Typeface mTypeface = Typeface.createFromAsset(getAssets(),"fonts/stolzl_regular.otf");
        mFastBookTv.setTypeface(mTypeface);

        mEventEndTimeTv = new TextView(this);

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
    public void setCircleTimeViewTime(String time) {
        mCtv.setTimerTimeText(time);
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
    public void setCurrentTimeText(String currentTime){
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
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        TwoLineTextButton[] buttons = new TwoLineTextButton[3];
        final int min = 5;
        for (int i = 0; i < 3; i++) {
            buttons[i] = new TwoLineTextButton(this, null);
            buttons[i].setLayoutParams(new LinearLayout.LayoutParams(250, 250));
            buttons[i].setTopText(min * (i+1) + "");
            buttons[i].setBottomText("min");
            mButtonsLayout.addView(buttons[i]);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(48, 0, 48, 0);
        buttons[1].setLayoutParams(params);
    }

    @Override
    public void showButtonsInOnHoldStatus() {
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Confirm button
        ImageButton mConfirmBtn = new ImageButton(this);
        mConfirmBtn.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        mConfirmBtn.setScaleType(ImageView.ScaleType.CENTER);
        mConfirmBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_oval_confirm));

        Drawable confirmBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_done_white, 80 ,60);
        mConfirmBtn.setImageDrawable(confirmBtnDrawable);
        LinearLayout mConfirmBtnWithText = ViewHelper.addTextUnderBtn(this, mConfirmBtn, "Confirm");
        mButtonsLayout.addView(mConfirmBtnWithText);

        // Fake button
        ImageButton mFakeBtn = new ImageButton(this);
        mFakeBtn.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        LinearLayout mFakeBtnWithText = ViewHelper.addTextUnderBtn(this, mFakeBtn, "fake");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 16, 0);
        mFakeBtnWithText.setLayoutParams(params);
        mFakeBtnWithText.setVisibility(View.INVISIBLE);
        mButtonsLayout.addView(mFakeBtnWithText);


        // Dismiss button
        ImageButton mDismissBtn  = new ImageButton(this);
        mDismissBtn.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        mDismissBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDismissBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle));

        Drawable dismissBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_clear_black, 50 ,50);
        mDismissBtn.setImageDrawable(dismissBtnDrawable);
        LinearLayout mDismissBtnWithText = ViewHelper.addTextUnderBtn(this, mDismissBtn, "Dismiss");
        mButtonsLayout.addView(mDismissBtnWithText);
    }

    @Override
    public void showButtonsInBusyStatus() {
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);

        LongPressButton mEndBnt = new LongPressButton(this, null);
        mEndBnt.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        mEndBnt.setScaleType(ImageView.ScaleType.CENTER);
        Drawable endBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_clear_white, 50 ,50);
        mEndBnt.setImageDrawable(endBtnDrawable);
        LinearLayout mEndBtnWithText = ViewHelper.addTextUnderBtn(this, mEndBnt, "Hold to end");
        mButtonsLayout.addView(mEndBtnWithText);

        ImageButton mDNDBtn  = new ImageButton(this);
        mDNDBtn.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        mDNDBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDNDBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle));

        Drawable DNDBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_donotdisturb_black, 50 ,70);
        mDNDBtn.setImageDrawable(DNDBtnDrawable);
        LinearLayout mDNDBtnWithText = ViewHelper.addTextUnderBtn(this, mDNDBtn, "Do not disturb");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(64, 0, 64, 0);
        mDNDBtnWithText.setLayoutParams(params);
        mButtonsLayout.addView(mDNDBtnWithText);

        ImageButton mExtentBtn  = new ImageButton(this);
        mExtentBtn.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        mExtentBtn.setScaleType(ImageView.ScaleType.CENTER);
        mExtentBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle));

        Drawable extentBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_add_black, 50 ,50);
        mExtentBtn.setImageDrawable(extentBtnDrawable);
        LinearLayout mExtentBtnWithText = ViewHelper.addTextUnderBtn(this, mExtentBtn, "Extent");
        mButtonsLayout.addView(mExtentBtnWithText);
    }

    @Override
    public void showButtonsInDoNotDisturbStatus() {
        mButtonsLayout.setOrientation(LinearLayout.VERTICAL);

        TextView busyUntilTv = new TextView(this);
        busyUntilTv.setText("BUSY UNTIL");
        busyUntilTv.setTextSize(26);
        busyUntilTv.setTextColor(Color.BLACK);
        Typeface stolzlRegular = Typeface.createFromAsset(getAssets(),"fonts/stolzl_regular.otf");
        busyUntilTv.setTypeface(stolzlRegular);
        mButtonsLayout.addView(busyUntilTv);

        mEventEndTimeTv.setTextSize(26);
        mEventEndTimeTv.setTextColor(Color.BLACK);
        Typeface stolzlMedium = Typeface.createFromAsset(getAssets(),"fonts/stolzl_regular.otf");
        mEventEndTimeTv.setTypeface(stolzlMedium);
        mButtonsLayout.addView(mEventEndTimeTv);

        mRoomEventsPresenter.updateCurrentTimeInDNDStatus();
    }

    @Override
    public void setEventEndText(String endTime) {
        if (endTime != null) {
            mEventEndTimeTv.setText(endTime);
        }
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
