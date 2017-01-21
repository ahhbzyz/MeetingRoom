package com.badoo.meetingroom.presentation.view.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.MyFirebaseMessagingService;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomStatusPresenter;
import com.badoo.meetingroom.presentation.view.fragment.EventCreatorDialogFragment;
import com.badoo.meetingroom.presentation.view.fragment.ImmersiveProgressDialogFragment;
import com.badoo.meetingroom.presentation.view.adapter.HorizontalTimelineAdapter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomStatusView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleView;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomStatusActivity extends BaseActivity implements RoomStatusView,
                                                                View.OnClickListener,
                                                                CircleView.OnCountDownListener,
                                                                HorizontalTimelineAdapter.OnItemClickListener {

    private static final int REQUEST_BOOK_ROOM = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;

    @Inject RoomStatusPresenter mPresenter;
    @Inject HorizontalTimelineAdapter mAdapter;

    // Typeface
    @Inject @Named("stolzl_regular") Typeface mStolzlRegularTypeface;
    @Inject @Named("stolzl_medium") Typeface mStolzlMediumTypeface;

    // Status bar
    @BindView(R.id.tv_current_date) TextView mCurrentDateTv;

    // Toolbar
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.img_calendar) ImageView mCalendarImg;
    @BindView(R.id.img_room) ImageView mRoomImg;

    // Circle time view
    @BindView(R.id.circle_view) CircleView mCircleView;

    @BindView(R.id.layout_circle_timer_info) LinearLayout mCircleTimerInfoLayout;
    @BindView(R.id.tv_room_status) TextView mRoomStatusTv;
    @BindView(R.id.tv_timer_one) TextView mTimerOneTv;
    @BindView(R.id.tv_timer_two) TextView mTimerTwoTv;
    @BindView(R.id.tv_timer_three) TextView mTimerThreeTv;
    @BindView(R.id.tv_timer_four) TextView mTimerFourTv;
    @BindView(R.id.tv_timer_colon) TextView mTimerColonTv;

    @BindView(R.id.layout_dnd) LinearLayout mDndLayout;
    @BindView(R.id.tv_dnd) TextView mDndTv;


    @BindView(R.id.img_book) ImageButton mCircleTimeViewBtn;

    // Horizontal timeline view
    @BindView(R.id.rv_horizontal_timeline) RecyclerView mHorizontalTimelineRv;

    // Button group
    @BindView(R.id.tv_fast_book) TextView mFastBookTv;
    @BindView(R.id.layout_btns) FrameLayout mButtonsLayout;

    // Top and bottom content
    @BindView(R.id.layout_top_content) LinearLayout mTopContentLayout;
    @BindView(R.id.layout_bottom_content) RelativeLayout mBottomContentLayout;

    // Dialogs
    private ImmersiveProgressDialogFragment mProgressDialog;
    private EventCreatorDialogFragment mEventOrganizerDialog;
    private Handler mLoadingDataDialogHandler;
    private LinearLayoutManager mLayoutManager;

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
        setUpCircleView();
        setUptCircleTimeTextViews();
        setUpHorizontalTimelineView();
        mPresenter.getEvents();
    }

    private void initViews() {
        // Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // TextViews
        mRoomNameTv.setTypeface(mStolzlMediumTypeface);
        mFastBookTv.setTypeface(mStolzlRegularTypeface);
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(this));
        mCalendarImg.setOnClickListener(this);
        mRoomImg.setOnClickListener(this);

        // Dialog
        mProgressDialog = ImmersiveProgressDialogFragment.newInstance();

        mEventOrganizerDialog = EventCreatorDialogFragment.newInstance();

        mLoadingDataDialogHandler = new Handler();

        mRoomStatusHandler = new RoomStatusHandler(this);


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("DD", "Key: " + key + " Value: " + value);
            }
        }

        startService(new Intent(this, MyFirebaseMessagingService.class));
    }

    private void setUptCircleTimeTextViews() {
        mRoomStatusTv.setTypeface(mStolzlRegularTypeface);
        mTimerOneTv.setTypeface(mStolzlMediumTypeface);
        mTimerTwoTv.setTypeface(mStolzlMediumTypeface);
        mTimerThreeTv.setTypeface(mStolzlMediumTypeface);
        mTimerFourTv.setTypeface(mStolzlMediumTypeface);
        mTimerColonTv.setTypeface(mStolzlMediumTypeface);
        mDndTv.setTypeface(mStolzlRegularTypeface);
    }

    private void setUpCircleView() {
        mCircleView.setTailIconDrawable(R.drawable.ic_arrow_left_white);
        mCircleView.setOnCountDownListener(this);
        mCircleView.setOnClickListener(this);

        mCircleTimeViewBtn.setOnClickListener(this);
    }

    private void setUpHorizontalTimelineView() {
        mAdapter.setOnItemClickListener(this);
        mHorizontalTimelineRv.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mHorizontalTimelineRv.setLayoutManager(mLayoutManager);
        Handler scrollBackHandler = new Handler();
        mHorizontalTimelineRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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

//        // Adjust current time text
//        mCurrentTimeTv.measure(0, 0);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCurrentTimeTv.getLayoutParams();
//        params.setMargins((int) (-mCurrentTimeTv.getMeasuredWidth() / 2f), params.topMargin, params.rightMargin, params.bottomMargin);
//        mCurrentTimeTv.setLayoutParams(params);
    }

    @Override
    public void onEventItemClicked(int position) {
        mPresenter.onEventClicked(position);
    }

    @Override
    public void onClick(View v) {
        ActivityOptions options = ActivityOptions
            .makeSceneTransitionAnimation(this, mRoomNameTv, "roomName");
        switch (v.getId()) {
            case R.id.img_calendar:
                Intent calendarIntent = new Intent(RoomStatusActivity.this, EventsCalendarActivity.class);
                startActivityForResult(calendarIntent, REQUEST_BOOK_ROOM, options.toBundle());
                break;
            case R.id.img_room:
                Intent roomListIntent = new Intent(RoomStatusActivity.this, AllRoomActivity.class);
                startActivityForResult(roomListIntent, REQUEST_BOOK_ROOM, options.toBundle());
                break;
            case R.id.circle_view:
                mPresenter.setDoNotDisturb(false);
                break;
            case R.id.img_book:
                mPresenter.circleTimeViewBtnClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void renderRoomEvent(EventModel currentEvent) {
        updateCircleTimeViewStatus(currentEvent);
        startCircleViewAnimator(currentEvent);
    }

    @Override
    public void startCircleViewAnimator(EventModel currentEvent) {
        if (currentEvent == null) {
            return;
        }
        if (currentEvent.isOnHold()) {
            mCircleView.startCountDownTimer(currentEvent.getStartTime(), currentEvent.getStartTime() + currentEvent.getOnHoldTime());
        } else {
            mCircleView.startCountDownTimer(currentEvent.getStartTime(), currentEvent.getEndTime());
        }
    }

    @Override
    public void renderRoomEventList(List<EventModel> roomEventModelList) {
        mAdapter.setEventList(roomEventModelList);
    }

    @Override
    public void updateCircleTimeViewStatus(EventModel currentEvent) {
        mRoomStatusHandler.updateCircleTimeViewStatus(currentEvent);
    }

    @Override
    public void updateHorizontalTimeline(int currentEventPosition) {
        float leftMargin = -(TimeHelper.getCurrentTimeSinceMidNight() * mAdapter.getWidthPerMillis())
            - currentEventPosition * getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_time_slot_divider_width)
            + getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_current_time_mark_left_margin);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHorizontalTimelineRv.getLayoutParams();
        params.setMargins((int)leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        mHorizontalTimelineRv.setLayoutParams(params);
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
        ActivityOptions options = ActivityOptions
            .makeSceneTransitionAnimation(this, mRoomNameTv, "roomName");
        startActivityForResult(intent, REQUEST_BOOK_ROOM, options.toBundle());
    }

    @Override
    public void showEventOrganizerDialog(EventModel event) {
        if (mEventOrganizerDialog != null) {
            mEventOrganizerDialog.setEvent(event);
            mEventOrganizerDialog.show(this);
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
    public void stopCountDown() {
        mCircleView.stopCircleViewCountDown();
    }

    @Override
    public void onCountDownTicking(long millisUntilFinished) {
        mRoomStatusHandler.onCountDownTicking(millisUntilFinished);
    }

    @Override
    public void onCountDownFinished() {
        mPresenter.onCountDownFinished();
    }

    @Override
    public void showLoadingData(String message) {
        mLoadingDataDialogHandler.postDelayed(() -> {
            mProgressDialog.setMessage(message);
            mProgressDialog.show(this);
        }, SHOW_LOADING_DIALOG_WAIT_TIME);
    }

    @Override
    public void dismissLoadingData() {
        mLoadingDataDialogHandler.removeCallbacksAndMessages(null);
        if (mProgressDialog != null && mProgressDialog.getDialog() != null) {
            mProgressDialog.dismiss();
        }
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
    protected void onRestart() {
        super.onRestart();
        if (mPresenter != null) {
            mPresenter.onRestart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountDown();
    }

    @Override
    protected void onSystemTimeRefresh() {
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(RoomStatusActivity.this));
        mPresenter.onSystemTimeRefresh();
    }
}
