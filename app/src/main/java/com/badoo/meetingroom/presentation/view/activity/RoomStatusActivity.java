package com.badoo.meetingroom.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.badoo.meetingroom.presentation.presenter.intf.RoomStatusPresenter;
import com.badoo.meetingroom.presentation.view.adapter.HorizontalTimelineAdapter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomEventsView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleTimerView;
import com.badoo.meetingroom.presentation.view.component.button.LongPressButton;
import com.badoo.meetingroom.presentation.view.component.button.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.viewutils.ViewHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomStatusActivity extends BaseActivity implements RoomEventsView, CircleTimerView.OnCountDownListener {

    private static final int REQUEST_BOOK_ROOM = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;

    @Inject RoomStatusPresenter mPresenter;
    @Inject HorizontalTimelineAdapter mAdapter;
    @Inject Handler mLoadingDataDialogHandler;

    // Typeface
    @Inject @Named("stolzl_regular") Typeface mStolzlRegularTypeface;
    @Inject @Named("stolzl_medium") Typeface mStolzMediumTypeface;

    // Status bar
    @BindView(R.id.tv_current_date) TextView mCurrentDateTv;

    // Toolbar
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.img_calendar) ImageView mCalendarImg;

    // Circle time view
    @BindView(R.id.ctv_status) CircleTimerView mCircleTimeView;
    @BindView(R.id.img_book) ImageButton mCircleTimeViewBtn;

    // Horizontal timeline view
    @BindView(R.id.rv_horizontal_timeline) RecyclerView mHorizontalTimelineRv;
    @BindView(R.id.tv_current_time) TextView mCurrentTimeTv;
    @BindView(R.id.view_time_mark) View mTimeMarkView;

    // Button group
    @BindView(R.id.tv_fast_book) TextView mFastBookTv;
    @BindView(R.id.layout_btns) LinearLayout mButtonsLayout;

    // Top and bottom content
    @BindView(R.id.layout_top_content) LinearLayout mTopContentLayout;
    @BindView(R.id.layout_bottom_content) RelativeLayout mBottomContentLayout;

    // Dialogs
    @Inject ProgressDialog mProgressDialog;
    private AlertDialog mEventOrganizerDialog;


    private final int mCircleBtnDiameter = (int) getResources().getDimension(R.dimen.circle_button_diameter);
    private final int mCircleBtnLeftMargin = (int) getResources().getDimension(R.dimen.circle_button_left_margin);
    private final int mCircleBtnNameTopMargin = (int) getResources().getDimension(R.dimen.circle_button_name_top_margin);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_status);
        ButterKnife.bind(this);
        getComponent().inject(this);
        mPresenter.setView(this);

        initViews();
        setUpCircleTimeView();
        setUpHorizontalTimelineView();
        registerTimeRefreshReceiver();

        mPresenter.getEvents();
    }

    private void initViews() {
        // Toolbar
        setSupportActionBar(mToolbar);

        // TextViews
        mRoomNameTv.setTypeface(mStolzMediumTypeface);
        mFastBookTv.setTypeface(mStolzlRegularTypeface);
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(this));

        // Dialog
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        View content = getLayoutInflater().inflate(R.layout.dialog_event_organizer, mBottomContentLayout);
        mEventOrganizerDialog = new AlertDialog.Builder(this, R.style.MyEventOrganizerDialog).setView(content).create();
        ((TextView) content.findViewById(R.id.tv_event_period)).setTypeface(mStolzlRegularTypeface);

        // Calendar Image
        mCalendarImg.setOnClickListener(v -> {
            Intent intent = new Intent(RoomStatusActivity.this, EventsCalendarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, REQUEST_BOOK_ROOM);
        });
    }

    private void setUpCircleTimeView() {
        mCircleTimeView.setTailIconDrawable(R.drawable.ic_arrow_left_white);
        mCircleTimeView.setCircleBtnIconDrawable(R.drawable.btn_oval_confirm);
        mCircleTimeView.setAlertIconDrawable(R.drawable.ic_alert_white);
        mCircleTimeView.setOnCountDownListener(this);
        mCircleTimeView.setOnClickListener(v -> {
            // TODO check clickable
            mPresenter.setDoNotDisturb(false);
            mTopContentLayout.animate().translationY(0);
            mBottomContentLayout.animate().translationY(0);
        });

        // Circle time view button
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCircleTimeViewBtn.getLayoutParams();
        mCircleTimeView.measure(0, 0);
        params.setMargins(0, 0, 0, (int) (mCircleTimeView.getMeasuredHeight() / 4f + mCircleTimeViewBtn.getLayoutParams().width / 2f));
        mCircleTimeViewBtn.setLayoutParams(params);
        mCircleTimeViewBtn.setOnClickListener(v -> mPresenter.circleTimeViewBtnClick());
    }

    private void setUpHorizontalTimelineView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHorizontalTimelineRv.setLayoutManager(manager);
        mHorizontalTimelineRv.setAdapter(mAdapter);

        // Adjust current time text
        mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
        mCurrentTimeTv.measure(0, 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCurrentTimeTv.getLayoutParams();
        params.setMargins((int) (-mCurrentTimeTv.getMeasuredWidth() / 2f), params.topMargin, params.rightMargin, params.bottomMargin);
        mCurrentTimeTv.setLayoutParams(params);
    }

    private void registerTimeRefreshReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mTimeRefreshReceiver,
            new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void renderNextRoomEvent(RoomEventModel nextEvent) {
        updateCircleTimeView(nextEvent);
        mCircleTimeView.startCountDownTimer(nextEvent.getStartTime(), nextEvent.getDuration());
    }

    @Override
    public void renderRoomEvents(List<RoomEventModel> roomEventModelList) {
        mAdapter.setEventList(roomEventModelList);
    }

    @Override
    public void updateHorizontalTimelineView(int numOfExpiredEvents) {
        float leftMargin = (TimeHelper.getCurrentTimeSinceMidNight()) * mAdapter.getWidthPerMillis()
            + (numOfExpiredEvents + 1) * getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_time_slot_divider_width)
            - getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_current_time_mark_left_margin)
            - getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_current_time_mark_width);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHorizontalTimelineRv.getLayoutParams();
        params.setMargins(Math.round(-leftMargin), params.topMargin, params.rightMargin, params.bottomMargin);
        mHorizontalTimelineRv.setLayoutParams(params);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCircleTimeView(RoomEventModel mCurrentEvent) {
        if (mCurrentEvent == null) {
            return;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(mCurrentEvent.getRemainingTime());
        if (hours >= 2) {
            mCircleTimeView.setTimerTimeText("2H+");
        }
        else {
            mCircleTimeView.setTimerTimeText(mCurrentEvent.getRemainingTimeInText());
        }

        mCircleTimeView.setAlertIconVisibility(mCurrentEvent.isDoNotDisturb());
        mCircleTimeView.setDNDVisibility(mCurrentEvent.isDoNotDisturb());
        mCircleTimeView.setTimerTimeVisibility(!mCurrentEvent.isDoNotDisturb());
        mCircleTimeView.setCircleBtnVisibility(!mCurrentEvent.isDoNotDisturb() && !mCurrentEvent.isOnHold());
        mCircleTimeView.setTailIconVisibility(mCurrentEvent.isConfirmed() || mCurrentEvent.isOnHold());

        switch (mCurrentEvent.getStatus()) {
            case RoomEventModel.AVAILABLE:
                mCircleTimeView.setTimerInfoText(getString(R.string.available_for_upper_case));
                mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_normal));
                mCircleTimeView.setBgCircleColor(mCurrentEvent.getEventBgColor());
                mCircleTimeView.setArcColor(mCurrentEvent.getEventColor());
                mCircleTimeView.setBgCirclePaintStyle(Paint.Style.STROKE);
                break;
            case RoomEventModel.BUSY:
                if (mCurrentEvent.isOnHold()) {
                    mCircleTimeView.setTimerInfoText(getString(R.string.on_hold_for_upper_case));
                    mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_normal));
                    mCircleTimeView.setBgCircleColor(mCurrentEvent.getEventBgColor());
                    mCircleTimeView.setArcColor(mCurrentEvent.getEventColor());
                    mCircleTimeView.setBgCirclePaintStyle(Paint.Style.STROKE);

                } else if (mCurrentEvent.isDoNotDisturb()) {
                    mCircleTimeView.setTimerInfoText(getString(R.string.do_not_disturb_upper_case));
                    mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_dnd));
                    mCircleTimeView.setBgCircleColor(mCurrentEvent.getEventColor());
                    mCircleTimeView.setArcColor(mCurrentEvent.getEventColor());
                    mCircleTimeView.setBgCirclePaintStyle(Paint.Style.FILL_AND_STROKE);

                } else {
                    mCircleTimeView.setTimerTimeText(mCurrentEvent.getEndTimeInText());
                    mCircleTimeView.setTimerInfoText(getString(R.string.busy_until_upper_case));
                    mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_dnd));
                    mCircleTimeView.setBgCircleColor(mCurrentEvent.getEventBgColor());
                    mCircleTimeView.setArcColor(mCurrentEvent.getEventColor());
                    mCircleTimeView.setBgCirclePaintStyle(Paint.Style.STROKE);

                }
                break;
            default:
                break;
        }
        mCircleTimeView.invalidate();
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
    public void showButtonsForAvailableStatus() {

        mFastBookTv.setVisibility(View.VISIBLE);

        // Update button group layout
        mButtonsLayout.removeAllViews();
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonsLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        // Circle time view button
        int btnImgWidth = (int) getResources().getDimension(R.dimen.circle_time_view_btn_available_img_width);
        int btnImgHeight = (int) getResources().getDimension(R.dimen.circle_time_view_btn_available_img_height);
        mCircleTimeViewBtn.setVisibility(View.VISIBLE);
        mCircleTimeViewBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_add_black, btnImgWidth, btnImgHeight));

        TwoLineTextButton[] buttons = new TwoLineTextButton[3];
        final int min = 5;
        for (int i = 0; i < 3; i++) {
            buttons[i] = new TwoLineTextButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter);
            buttons[i].setLayoutParams(params);
            if (i == 1) {
                params.setMargins(mCircleBtnLeftMargin, 0, mCircleBtnLeftMargin, 0);
                buttons[1].setLayoutParams(params);
            }
            buttons[i].setTopText(min * (i + 1) + "");
            buttons[i].setBottomText("min");
            buttons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_time));
            mButtonsLayout.addView(buttons[i]);
            final int temp = i;
            buttons[i].setOnClickListener(v -> mPresenter.insertEvent(min * (temp + 1)));
        }
    }

    @Override
    public void showButtonsForOnHoldStatus() {

        mFastBookTv.setVisibility(View.GONE);

        // Update button group layout
        mButtonsLayout.removeAllViews();
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mCircleTimeViewBtn.setVisibility(View.INVISIBLE);

        // Confirm button
        ImageButton mConfirmBtn = new ImageButton(this);
        mConfirmBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mConfirmBtn.setScaleType(ImageView.ScaleType.CENTER);
        mConfirmBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_oval_confirm));
        int confirmBtnImgWidth = (int) getResources().getDimension(R.dimen.confirm_btn_img_width);
        int confirmBtnImgHeight = (int) getResources().getDimension(R.dimen.confirm_btn_img_height);
        mConfirmBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_done_white, confirmBtnImgWidth, confirmBtnImgHeight));
        LinearLayout mConfirmBtnWithText = ViewHelper.addTextUnderBtn(this, mConfirmBtn, getString(R.string.confirm), mCircleBtnNameTopMargin);
        mButtonsLayout.addView(mConfirmBtnWithText);
        mConfirmBtn.setOnClickListener(v -> mPresenter.setEventConfirmed());

        // Fake button
        ImageButton mFakeBtn = new ImageButton(this);
        mFakeBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(mCircleBtnLeftMargin, 0, mCircleBtnLeftMargin, 0);
        mFakeBtn.setLayoutParams(params);
        mFakeBtn.setVisibility(View.INVISIBLE);
        mButtonsLayout.addView(mFakeBtn);

        // Dismiss button
        ImageButton mDismissBtn = new ImageButton(this);
        mDismissBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mDismissBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDismissBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_dismiss));
        int dismissBtnImgWidth = (int) getResources().getDimension(R.dimen.dismiss_btn_img_width);
        int dismissBtnImgHeight = (int) getResources().getDimension(R.dimen.dismiss_btn_img_height);
        mDismissBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_clear_black, dismissBtnImgWidth, dismissBtnImgHeight));
        LinearLayout mDismissBtnWithText = ViewHelper.addTextUnderBtn(this, mDismissBtn, getString(R.string.dismiss), mCircleBtnNameTopMargin);
        mButtonsLayout.addView(mDismissBtnWithText);
        mDismissBtn.setOnClickListener(v -> mPresenter.deleteEvent());
    }

    @Override
    public void showButtonsForBusyStatus() {

        mFastBookTv.setVisibility(View.GONE);

        // Update button group layout
        mButtonsLayout.removeAllViews();
        mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        int ctvBtnImgWidth = (int) getResources().getDimension(R.dimen.circle_time_view_btn_busy_img_width);
        int ctvBtnImgHeight = (int) getResources().getDimension(R.dimen.circle_time_view_btn_busy_img_height);
        mCircleTimeViewBtn.setVisibility(View.VISIBLE);
        mCircleTimeViewBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_info_black, ctvBtnImgWidth, ctvBtnImgHeight));

        // Hold to end btn
        LongPressButton mEndBtn = new LongPressButton(this);
        int diameter = (int) (mCircleBtnDiameter + mEndBtn.getCountDownCircleWidth() * 4);
        mEndBtn.setLayoutParams(new LinearLayout.LayoutParams(diameter, diameter));
        mEndBtn.setScaleType(ImageView.ScaleType.CENTER);
        int endBtnImgWidth = (int) getResources().getDimension(R.dimen.end_btn_img_width);
        int endBtnImgHeight = (int) getResources().getDimension(R.dimen.end_btn_img_height);
        mEndBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_clear_white, endBtnImgWidth, endBtnImgHeight));
        LinearLayout mEndBtnWithText = ViewHelper.addTextUnderBtn(this, mEndBtn, getString(R.string.hold_to_end), (int) (mCircleBtnNameTopMargin - 2 * mEndBtn.getCountDownCircleWidth()));
        mEndBtnWithText.getChildAt(mEndBtnWithText.getChildCount() - 1).setPadding(0, 0, 0, (int) (2 * mEndBtn.getCountDownCircleWidth()));
        mButtonsLayout.addView(mEndBtnWithText);
        mEndBtn.setOnCountDownListener(() -> mPresenter.deleteEvent());

        // Do not disturb btn
        ImageButton mDNDBtn = new ImageButton(this);
        mDNDBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mDNDBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDNDBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_busy));
        int dndBtnImgWidth = (int) getResources().getDimension(R.dimen.dnd_btn_img_width);
        int dndBtnImgHeight = (int) getResources().getDimension(R.dimen.dnd_btn_img_height);
        mDNDBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_donotdisturb_black, dndBtnImgWidth, dndBtnImgHeight));
        LinearLayout mDNDBtnWithText = ViewHelper.addTextUnderBtn(this, mDNDBtn, getString(R.string.do_not_disturb), mCircleBtnNameTopMargin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(mCircleBtnLeftMargin, 0, mCircleBtnLeftMargin, 0);
        mDNDBtnWithText.setLayoutParams(params);
        mButtonsLayout.addView(mDNDBtnWithText);
        mDNDBtn.setOnClickListener(v -> {
            mPresenter.setDoNotDisturb(true);
            mTopContentLayout.animate().translationY(-mTopContentLayout.getHeight());
            mBottomContentLayout.animate().translationY(mBottomContentLayout.getHeight());
        });

        ImageButton mExtendBtn = new ImageButton(this);
        mExtendBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mExtendBtn.setScaleType(ImageView.ScaleType.CENTER);
        mExtendBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_circle_busy));
        int extendBtnImgWidth = (int) getResources().getDimension(R.dimen.extend_btn_img_width);
        int extendBtnImgHeight = (int) getResources().getDimension(R.dimen.extend_btn_img_height);
        mExtendBtn.setImageDrawable(ViewHelper.createScaleDrawable(this, R.drawable.ic_add_black, extendBtnImgWidth, extendBtnImgHeight));
        LinearLayout mExtendBtnWithText = ViewHelper.addTextUnderBtn(this, mExtendBtn, getString(R.string.extend), mCircleBtnNameTopMargin);
        mButtonsLayout.addView(mExtendBtnWithText);
        mExtendBtn.setOnClickListener(v -> mPresenter.updateEvent());
    }

    @Override
    public void showButtonsForDoNotDisturbStatus(String eventEndTime) {
        mFastBookTv.setVisibility(View.GONE);

        mBottomContentLayout.removeAllViews();
        mButtonsLayout.setOrientation(LinearLayout.VERTICAL);
        mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mCircleTimeViewBtn.setVisibility(View.INVISIBLE);

        TextView busyUntilTv = new TextView(this);
        busyUntilTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        busyUntilTv.setText(getString(R.string.busy_until));
        busyUntilTv.setTextSize(getResources().getDimension(R.dimen.dnd_status_busy_until_text_size));
        busyUntilTv.setTextColor(Color.BLACK);
        busyUntilTv.setTypeface(mStolzlRegularTypeface);
        busyUntilTv.setIncludeFontPadding(false);
        mButtonsLayout.addView(busyUntilTv);

        TextView mEventEndTimeTv = new TextView(this);
        mEventEndTimeTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mEventEndTimeTv.setText(eventEndTime);
        mEventEndTimeTv.setTextColor(Color.BLACK);
        mEventEndTimeTv.setTextSize(getResources().getDimension(R.dimen.dnd_status_current_time_text_size));
        mEventEndTimeTv.setIncludeFontPadding(false);
        mEventEndTimeTv.setTypeface(mStolzMediumTypeface);
        mButtonsLayout.addView(mEventEndTimeTv);
    }

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
                mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(RoomStatusActivity.this));
                mPresenter.updateHorizontalTimeline();
            }
        }
    };

    @Override
    public void onCountDownTicking(long millisUntilFinished) {
        mPresenter.onCountDownTicking(millisUntilFinished);
    }

    @Override
    public void onCountDownFinished() {
        mPresenter.onCountDownFinished();
    }

    @Override
    public void showEventOrganizerDialog() {
        if (mEventOrganizerDialog != null) {
            mEventOrganizerDialog.show();
            if (mEventOrganizerDialog.getWindow() != null) {
                mEventOrganizerDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Override
    public void showLoadingData(String message) {
        mLoadingDataDialogHandler.postDelayed(() -> {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }, 2000);
    }

    @Override
    public void dismissLoadingData() {
        mProgressDialog.dismiss();
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
    public void handleRecoverableAuthException(UserRecoverableAuthIOException e) {
        this.startActivityForResult(e.getIntent(), 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.getEvents();
                }
                break;
            case REQUEST_BOOK_ROOM:
                if (resultCode == RESULT_OK) {
                    mPresenter.getEvents();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimeRefreshReceiver);
        super.onDestroy();
    }
}
