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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.RoomEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleTimerView;
import com.badoo.meetingroom.presentation.view.component.button.LongPressButton;
import com.badoo.meetingroom.presentation.view.component.button.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.component.horizontaltimelineview.HorizontalTimelineView;
import com.badoo.meetingroom.presentation.view.viewutils.ViewHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.LinkedList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomEventsActivity extends BaseActivity implements RoomEventsView {

    private static final int REQUEST_BOOK_ROOM = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    @Inject
    RoomEventsPresenterImpl mPresenter;

    @BindView(R.id.ctv_status) CircleTimerView mCtv;
    @BindView(R.id.htv_room_events) HorizontalTimelineView mHtv;
    @BindView(R.id.layout_btns) LinearLayout mButtonsLayout;
    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tv_fast_book) TextView mFastBookTv;
    @BindView(R.id.img_book) ImageButton mCircleBtn;
    @BindView(R.id.img_calendar) ImageView mCalendarImg;

    private ProgressDialog mProgressDialog;

    private final int buttonDiameter = 200;
    private final int buttonMargin = 64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_events);
        ButterKnife.bind(this);
        this.getApplicationComponent().inject(this);

        setUpToolbar();
        setUpProgressDialog();
        setTextViews();
        setUpCircleTimeViewButton();
        setUpCircleTimeView();
        setUpCalendarImageButton();
        registerTimeRefreshReceiver();

        mPresenter.setView(this);
        mPresenter.init();

    }

    private void setUpProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Loading Data...");
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void setUpToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Typeface stolzlMedium = Typeface.createFromAsset(getAssets(),"fonts/stolzl_medium.otf");
        mRoomNameTv.setTypeface(stolzlMedium);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    private void setTextViews(){
        Typeface stolzlRegular = Typeface.createFromAsset(getAssets(),"fonts/stolzl_regular.otf");
        mFastBookTv.setTypeface(stolzlRegular);
    }

    private void setUpCircleTimeViewButton() {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCircleBtn.getLayoutParams();
        mCtv.measure(0, 0);
        params.setMargins(0, 0, 0, (int) (mCtv.getMeasuredHeight()/4f + mCircleBtn.getLayoutParams().width/2f));
        mCircleBtn.setLayoutParams(params);
        mCircleBtn.setOnClickListener(v -> {
            mPresenter.circleTimeViewBtnClick();
        });

    }

    private void setUpCalendarImageButton() {
        mCalendarImg.setOnClickListener(v -> {
            Intent intent = new Intent(RoomEventsActivity.this, EventsCalendarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, REQUEST_BOOK_ROOM);
        });
    }

    private void registerTimeRefreshReceiver() {
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return this.getApplicationContext();
    }



    @Override
    public void setCircleTimeViewTime(String time) {
        mCtv.setTimerTimeText(time);
    }

    public void setUpCircleTimeView() {
        mCtv.setTailIconDrawable(R.drawable.ic_arrow_left_white);
        mCtv.setCircleBtnIconDrawable(R.drawable.btn_oval_confirm);
        mCtv.setAlertIconDrawable(R.drawable.ic_alert_white);
        mCtv.setOnCountDownListener(mOnCountDownListener);
        mCtv.setOnClickListener(v -> {
            mPresenter.setDoNotDisturb(false);
        });
    }

    @Override
    public void renderNextRoomEvent(RoomEventModel nextEvent) {
        mCtv.startCountDownTimer(nextEvent);
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
        mFastBookTv.setVisibility(View.VISIBLE);
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonsLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        mCircleBtn.setVisibility(View.VISIBLE);
        Drawable addDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_add_black, 32 ,32);
        mCircleBtn.setImageDrawable(addDrawable);

        TwoLineTextButton[] buttons = new TwoLineTextButton[3];
        final int min = 5;
        for (int i = 0; i < 3; i++) {
            buttons[i] = new TwoLineTextButton(this, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter);
            buttons[i].setLayoutParams(params);
            if (i == 1) {
                params.setMargins(buttonMargin, 0, buttonMargin, 0);
                buttons[1].setLayoutParams(params);
            }
            buttons[i].setTopText(min * (i+1) + "");
            buttons[i].setBottomText("min");
            buttons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_time));
            mButtonsLayout.addView(buttons[i]);
            final int temp = i;
            buttons[i].setOnClickListener(v -> mPresenter.insertEvent(min * (temp + 1)));
        }
    }

    @Override
    public void showButtonsInOnHoldStatus() {
        mFastBookTv.setVisibility(View.GONE);
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mCircleBtn.setVisibility(View.INVISIBLE);

        // Confirm button
        ImageButton mConfirmBtn = new ImageButton(this);
        mConfirmBtn.setLayoutParams(new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter));
        mConfirmBtn.setScaleType(ImageView.ScaleType.CENTER);
        mConfirmBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_oval_confirm));

        Drawable confirmBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_done_white, 80 ,60);
        mConfirmBtn.setImageDrawable(confirmBtnDrawable);
        LinearLayout mConfirmBtnWithText = ViewHelper.addTextUnderBtn(this, mConfirmBtn, "Confirm");
        mButtonsLayout.addView(mConfirmBtnWithText);
        mConfirmBtn.setOnClickListener(v -> mPresenter.confirmEvent());

        // Fake button
        ImageButton mFakeBtn = new ImageButton(this);
        mFakeBtn.setLayoutParams(new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter));
        LinearLayout mFakeBtnWithText = ViewHelper.addTextUnderBtn(this, mFakeBtn, "fake");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(buttonMargin, 0, buttonMargin, 0);
        mFakeBtnWithText.setLayoutParams(params);
        mFakeBtnWithText.setVisibility(View.INVISIBLE);
        mButtonsLayout.addView(mFakeBtnWithText);


        // Dismiss button
        ImageButton mDismissBtn  = new ImageButton(this);
        mDismissBtn.setLayoutParams(new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter));
        mDismissBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDismissBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_dismiss));

        Drawable dismissBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_clear_black, 50 ,50);
        mDismissBtn.setImageDrawable(dismissBtnDrawable);
        LinearLayout mDismissBtnWithText = ViewHelper.addTextUnderBtn(this, mDismissBtn, "Dismiss");
        mButtonsLayout.addView(mDismissBtnWithText);
        mDismissBtn.setOnClickListener(v -> mPresenter.dismissEvent());
    }

    @Override
    public void showButtonsInBusyStatus() {
        mFastBookTv.setVisibility(View.GONE);
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mCircleBtn.setVisibility(View.VISIBLE);
        Drawable addDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_info_black, 12 ,48);
        mCircleBtn.setImageDrawable(addDrawable);

        // Hold to end btn
        LongPressButton mEndBnt = new LongPressButton(this, null);
        mEndBnt.setLayoutParams(new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter));
        mEndBnt.setScaleType(ImageView.ScaleType.CENTER);
        Drawable endBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_clear_white, 50 ,50);
        mEndBnt.setImageDrawable(endBtnDrawable);
        LinearLayout mEndBtnWithText = ViewHelper.addTextUnderBtn(this, mEndBnt, "Hold to end");
        mButtonsLayout.addView(mEndBtnWithText);

        // Do not disturb btn
        ImageButton mDNDBtn  = new ImageButton(this);
        mDNDBtn.setLayoutParams(new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter));
        mDNDBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDNDBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_busy));
        Drawable DNDBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_donotdisturb_black, 50 ,70);
        mDNDBtn.setImageDrawable(DNDBtnDrawable);
        LinearLayout mDNDBtnWithText = ViewHelper.addTextUnderBtn(this, mDNDBtn, "Do not disturb");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(buttonMargin, 0, buttonMargin, 0);
        mDNDBtnWithText.setLayoutParams(params);
        mButtonsLayout.addView(mDNDBtnWithText);
        mDNDBtn.setOnClickListener(v -> mPresenter.setDoNotDisturb(true));

        ImageButton mExtendBtn  = new ImageButton(this);
        mExtendBtn.setLayoutParams(new LinearLayout.LayoutParams(buttonDiameter, buttonDiameter));
        mExtendBtn.setScaleType(ImageView.ScaleType.CENTER);
        mExtendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_busy));

        Drawable extendBtnDrawable = ViewHelper.createScaleDrawable(this, R.drawable.ic_add_black, 50 ,50);
        mExtendBtn.setImageDrawable(extendBtnDrawable);
        LinearLayout mExtendBtnWithText = ViewHelper.addTextUnderBtn(this, mExtendBtn, "Extend");
        mButtonsLayout.addView(mExtendBtnWithText);
        mExtendBtn.setOnClickListener(v -> {
            mPresenter.extendBookingPeriod();
        });
    }

    @Override
    public void showButtonsInDoNotDisturbStatus(String eventEndTime) {
        mFastBookTv.setVisibility(View.GONE);
        mButtonsLayout.setOrientation(LinearLayout.VERTICAL);
        mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mCircleBtn.setVisibility(View.INVISIBLE);

        TextView busyUntilTv = new TextView(this);
        busyUntilTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        busyUntilTv.setText("BUSY UNTIL");
        busyUntilTv.setTextSize(30);
        busyUntilTv.setTextColor(Color.BLACK);
        Typeface stolzlRegular = Typeface.createFromAsset(getAssets(),"fonts/stolzl_regular.otf");
        busyUntilTv.setTypeface(stolzlRegular);
        busyUntilTv.setIncludeFontPadding(false);
        mButtonsLayout.addView(busyUntilTv);

        TextView mEventEndTimeTv = new TextView(this);
        mEventEndTimeTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mEventEndTimeTv.setText(eventEndTime);
        mEventEndTimeTv.setTextColor(Color.BLACK);
        mEventEndTimeTv.setTextSize(72);
        mEventEndTimeTv.setIncludeFontPadding(false);
        Typeface stolzlMedium = Typeface.createFromAsset(getAssets(),"fonts/stolzl_medium.otf");
        mEventEndTimeTv.setTypeface(stolzlMedium);
        mButtonsLayout.addView(mEventEndTimeTv);
    }

    @Override
    public void updateEventStatus() {
        mCtv.updateCurrentStatus();
        mHtv.updateCurrentStatus();
    }

    @Override
    public void showRecoverableAuth(UserRecoverableAuthIOException e) {
        this.startActivityForResult(e.getIntent(), 1000);
    }

    @Override
    public void bookRoom(long startTime, long endTime) {
        Intent intent = new Intent(this, RoomBookingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("startTime", startTime);
        bundle.putLong("endTime", endTime);
        intent.putExtra("timePeriod", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, REQUEST_BOOK_ROOM);
    }

    @Override
    public void showBookingSuccessful() {
        Toast.makeText(this, "Room is booked successfully", Toast.LENGTH_SHORT).show();
        mPresenter.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.init();
                }
                break;
            case REQUEST_BOOK_ROOM:
                if (resultCode == RESULT_OK) {
                    mPresenter.init();
                }
                break;
        }
    }

    private CircleTimerView.OnCountDownListener mOnCountDownListener =
        new CircleTimerView.OnCountDownListener() {
            @Override
            public void onCountDownTicking(long millisUntilFinished) {
                mPresenter.onCountDownTicking(millisUntilFinished);
            }

            @Override
            public void onCountDownFinished() {
                mPresenter.onCountDownFinished();
            }
        };


    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mPresenter.updateCurrentTimeForHtv();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeRefreshReceiver);
    }
}
