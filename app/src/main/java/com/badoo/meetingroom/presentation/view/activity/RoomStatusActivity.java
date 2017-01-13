package com.badoo.meetingroom.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomStatusActivity extends BaseActivity implements RoomEventsView, View.OnClickListener, CircleTimerView.OnCountDownListener, HorizontalTimelineAdapter.OnItemClickListener {

    private static final int REQUEST_BOOK_ROOM = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;

    @Inject RoomStatusPresenter mPresenter;
    @Inject HorizontalTimelineAdapter mAdapter;

    // Typeface
    @Inject @Named("stolzl_regular") Typeface mStolzlRegularTypeface;
    @Inject @Named("stolzl_medium") Typeface mStolzMediumTypeface;

    // Status bar
    @BindView(R.id.tv_current_date) TextView mCurrentDateTv;

    // Toolbar
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.img_calendar) ImageView mCalendarImg;
    @BindView(R.id.img_room) ImageView mRoomImg;

    // Circle time view
    @BindView(R.id.circle_time_view) CircleTimerView mCircleTimeView;
    @BindView(R.id.img_book) ImageButton mCircleTimeViewBtn;

    // Horizontal timeline view
    @BindView(R.id.rv_horizontal_timeline) RecyclerView mHorizontalTimelineRv;
    @BindView(R.id.tv_current_time) TextView mCurrentTimeTv;
    @BindView(R.id.layout_current_time) View mCurrentTimeLayout;

    // Button group
    @BindView(R.id.tv_fast_book) TextView mFastBookTv;
    @BindView(R.id.layout_btns) LinearLayout mButtonsLayout;

    // Top and bottom content
    @BindView(R.id.layout_top_content) LinearLayout mTopContentLayout;
    @BindView(R.id.layout_bottom_content) RelativeLayout mBottomContentLayout;

    // Dialogs
    private ProgressDialog mProgressDialog;
    private Handler mLoadingDataDialogHandler;
    private AlertDialog mEventOrganizerDialog;

    final long SCROLL_BACK_WAIT_TIME = 3000;
    final long SHOW_LOADING_DIALOG_WAIT_TIME = 1000;

    private RoomStatusHandler mRoomStatusHandler;

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

        mRoomStatusHandler = new RoomStatusHandler(this);

        mPresenter.getEvents();
    }

    private void initViews() {
        // Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // TextViews
        mRoomNameTv.setTypeface(mStolzMediumTypeface);
        mFastBookTv.setTypeface(mStolzlRegularTypeface);
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(this));
        mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
        mCalendarImg.setOnClickListener(this);
        mRoomImg.setOnClickListener(this);

        // Dialog
        mLoadingDataDialogHandler = new Handler();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        View content = View.inflate(this, R.layout.dialog_event_organizer, null);
        mEventOrganizerDialog = new AlertDialog.Builder(this, R.style.MyEventOrganizerDialog).setView(content).create();
        ((TextView) content.findViewById(R.id.tv_event_period)).setTypeface(mStolzlRegularTypeface);
    }

    private void setUpCircleTimeView() {
        mCircleTimeView.setTailIconDrawable(R.drawable.ic_arrow_left_white);
        mCircleTimeView.setCircleBtnIconDrawable(R.drawable.btn_oval_confirm);
        mCircleTimeView.setAlertIconDrawable(R.drawable.ic_alert_white);
        mCircleTimeView.setOnCountDownListener(this);
        mCircleTimeView.setOnClickListener(this);

        // Circle time view button
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCircleTimeViewBtn.getLayoutParams();
        mCircleTimeView.measure(0, 0);
        params.setMargins(0, 0, 0, (int) (mCircleTimeView.getMeasuredHeight() / 4f + mCircleTimeViewBtn.getLayoutParams().width / 2f));
        mCircleTimeViewBtn.setLayoutParams(params);
        mCircleTimeViewBtn.setOnClickListener(v -> mPresenter.circleTimeViewBtnClick());
    }

    private void setUpHorizontalTimelineView() {
        mAdapter.setOnItemClickListener(this);
        mHorizontalTimelineRv.setAdapter(mAdapter);
        mHorizontalTimelineRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Handler scrollBackHandler = new Handler();
        mHorizontalTimelineRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float startX = mCurrentTimeLayout.getX();
                mCurrentTimeLayout.setX((int) (startX - dx));
                scrollBackHandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    scrollBackHandler.postDelayed(() -> {
                        if (mHorizontalTimelineRv.computeHorizontalScrollOffset() != 0) {
                            mHorizontalTimelineRv.smoothScrollToPosition(0);
                        }
                    }, SCROLL_BACK_WAIT_TIME);
                }
            }
        });

        // Adjust current time text
        mCurrentTimeTv.measure(0, 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCurrentTimeTv.getLayoutParams();
        params.setMargins((int) (-mCurrentTimeTv.getMeasuredWidth() / 2f), params.topMargin, params.rightMargin, params.bottomMargin);
        mCurrentTimeTv.setLayoutParams(params);
    }

    @Override
    public void renderRoomEvent(RoomEventModel currentEvent) {
        updateCircleTimeViewStatus(currentEvent);
        if (currentEvent.isOnHold()) {
            mCircleTimeView.startCountDownTimer(currentEvent.getStartTime(), currentEvent.getStartTime() + currentEvent.getOnHoldTime());
        } else {
            mCircleTimeView.startCountDownTimer(currentEvent.getStartTime(), currentEvent.getEndTime());
        }
    }

    @Override
    public void renderRoomEventList(List<RoomEventModel> roomEventModelList) {
        mAdapter.setEventList(roomEventModelList);
    }

    @Override
    public void updateCircleTimeViewTime(String text) {
        mCircleTimeView.setTimerTimeText(text);
    }

    @Override
    public void updateCircleTimeViewStatus(RoomEventModel currentEvent) {
        if (currentEvent == null) {
            return;
        }
        mCircleTimeView.setAlertIconVisibility(currentEvent.isDoNotDisturb());
        mCircleTimeView.setDNDVisibility(currentEvent.isDoNotDisturb());
        mCircleTimeView.setTimerTimeVisibility(!currentEvent.isDoNotDisturb());
        mCircleTimeView.setCircleBtnVisibility(!currentEvent.isDoNotDisturb() && !currentEvent.isOnHold());
        mCircleTimeView.setTailIconVisibility(!currentEvent.isAvailable() && !currentEvent.isDoNotDisturb());

        switch (currentEvent.getStatus()) {
            case RoomEventModel.AVAILABLE:
                mCircleTimeView.setTimerInfoText(getString(R.string.available_for_upper_case));
                mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_normal));
                mCircleTimeView.setBgCircleColor(currentEvent.getEventBgColor());
                mCircleTimeView.setArcColor(currentEvent.getEventColor());
                mCircleTimeView.setBgCirclePaintStyle(Paint.Style.STROKE);
                break;
            case RoomEventModel.BUSY:
                if (currentEvent.isOnHold()) {
                    mCircleTimeView.setTimerInfoText(getString(R.string.on_hold_for_upper_case));
                    mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_normal));
                    mCircleTimeView.setBgCircleColor(currentEvent.getEventBgColor());
                    mCircleTimeView.setArcColor(currentEvent.getEventColor());
                    mCircleTimeView.setBgCirclePaintStyle(Paint.Style.STROKE);
                } else if (currentEvent.isDoNotDisturb()) {
                    mCircleTimeView.setTimerInfoText(getString(R.string.do_not_disturb_upper_case));
                    mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_dnd));
                    mCircleTimeView.setBgCircleColor(currentEvent.getEventColor());
                    mCircleTimeView.setArcColor(currentEvent.getEventColor());
                    mCircleTimeView.setBgCirclePaintStyle(Paint.Style.FILL_AND_STROKE);
                } else {
                    mCircleTimeView.setTimerTimeText(currentEvent.getEndTimeInText());
                    mCircleTimeView.setTimerInfoText(getString(R.string.busy_until_upper_case));
                    mCircleTimeView.setTimerInfoTextColor(ContextCompat.getColor(this, R.color.ctv_info_text_color_normal));
                    mCircleTimeView.setBgCircleColor(currentEvent.getEventBgColor());
                    mCircleTimeView.setArcColor(currentEvent.getEventColor());
                    mCircleTimeView.setBgCirclePaintStyle(Paint.Style.STROKE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void updateHorizontalTimelineCurrentTime() {
        mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
    }

    @Override
    public void updateHorizontalTimelinePosition(int numOfExpiredEvents) {

        float leftMargin = (TimeHelper.getCurrentTimeSinceMidNight()) * mAdapter.getWidthPerMillis()
            + (numOfExpiredEvents + 1) * getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_time_slot_divider_width)
            - getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_current_time_mark_left_margin);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHorizontalTimelineRv.getLayoutParams();
        params.setMargins((int) -leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        mHorizontalTimelineRv.setLayoutParams(params);
    }

    @Override
    public void restartCountDownTimer(RoomEventModel currentEvent) {
        if (currentEvent != null && currentEvent.isOnHold()) {
            mCircleTimeView.startCountDownTimer(currentEvent.getStartTime(), currentEvent.getStartTime() + currentEvent.getOnHoldTime());
        } else {
            mCircleTimeView.startCountDownTimer(currentEvent.getStartTime(), currentEvent.getEndTime());
        }
    }

    @Override
    public void updateRecyclerView() {
        mAdapter.notifyDataSetChanged();
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
    public void showEventOrganizerDialog(RoomEventModel mCurrentEvent) {
        if (mEventOrganizerDialog != null) {
            mEventOrganizerDialog.show();
            if (mEventOrganizerDialog.getWindow() != null) {
                mEventOrganizerDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Override
    public void showButtonGroupForAvailableStatus() {
        mRoomStatusHandler.showButtonGroupForAvailableStatus();
    }

    @Override
    public void showButtonGroupForOnHoldStatus() {
        mRoomStatusHandler.showButtonGroupForOnHoldStatus();
    }

    @Override
    public void showButtonGroupForBusyStatus() {
        mRoomStatusHandler.showButtonGroupForBusyStatus();
    }

    @Override
    public void showButtonGroupForDoNotDisturbStatus(String endTimeInText) {
        mRoomStatusHandler.showButtonGroupForDoNotDisturbStatus(endTimeInText);
    }

    @Override
    public void onEventItemClicked(int position) {
        mPresenter.onEventClicked(position);
    }

    @Override
    public void hideTopBottomContent() {
        mTopContentLayout.animate().translationY(-mTopContentLayout.getHeight());
        mBottomContentLayout.animate().translationY(mBottomContentLayout.getHeight());
    }

    @Override
    public void showTopBottomContent() {
        mTopContentLayout.animate().translationY(0);
        mBottomContentLayout.animate().translationY(0);
    }

    @Override
    public void onCountDownTicking(long millisUntilFinished) {
        mPresenter.onCountDownTicking(millisUntilFinished);
    }

    @Override
    public void onCountDownFinished() {
        mPresenter.onCountDownFinished();
    }

    @Override
    public void showLoadingData(String message) {
        mLoadingDataDialogHandler.postDelayed(() -> {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }, SHOW_LOADING_DIALOG_WAIT_TIME);
    }

    @Override
    public void dismissLoadingData() {
        mLoadingDataDialogHandler.removeCallbacksAndMessages(null);
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
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mPresenter != null) {
            mPresenter.restartCountDown();
            mPresenter.onSystemTimeUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimeRefreshReceiver != null) {
            mCircleTimeView.stopCountDown();
            unregisterReceiver(mTimeRefreshReceiver);
        }
    }

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
                mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(RoomStatusActivity.this));
                mPresenter.onSystemTimeUpdate();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_calendar:
                Intent calendarIntent = new Intent(RoomStatusActivity.this, EventsCalendarActivity.class);
                calendarIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(calendarIntent, REQUEST_BOOK_ROOM);
                break;
            case R.id.img_room:
                Intent roomListIntent = new Intent(RoomStatusActivity.this, AllRoomsActivity.class);
                startActivity(roomListIntent);
                break;
            case R.id.circle_time_view:
                mPresenter.setDoNotDisturb(false);
                break;
            default:
                break;
        }
    }
}
