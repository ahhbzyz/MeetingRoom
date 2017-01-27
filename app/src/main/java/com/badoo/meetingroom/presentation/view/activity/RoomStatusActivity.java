package com.badoo.meetingroom.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomStatusPresenter;
import com.badoo.meetingroom.presentation.view.adapter.HorizontalEventItemDecoration;
import com.badoo.meetingroom.presentation.view.fragment.EventCreatorDialogFragment;
import com.badoo.meetingroom.presentation.view.fragment.ProgressDialogFragment;
import com.badoo.meetingroom.presentation.view.adapter.HorizontalTimelineAdapter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomStatusView;
import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleView;
import com.badoo.meetingroom.presentation.view.viewutils.ThemeHelper;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public class RoomStatusActivity extends BaseActivity implements RoomStatusView, View.OnClickListener, CircleView.OnCountDownListener, HorizontalTimelineAdapter.OnEventClickListener {

    private static final int REQUEST_BOOK_ROOM = 1000;

    @Inject RoomStatusPresenter mPresenter;
    @Inject HorizontalTimelineAdapter mAdapter;

    // Typeface
    @Inject @Named("stolzl_regular") Typeface mStolzlRegularTypeface;
    @Inject @Named("stolzl_medium") Typeface mStolzlMediumTypeface;

    // Status bar
    @BindView(R.id.tv_current_date_time) TextView mCurrentDateTimeTv;

    // Toolbar
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.img_calendar) ImageView mCalendarImg;
    @BindView(R.id.img_room) ImageView mRoomImg;
    @BindView(R.id.img_night_mode) ImageView mNightModeImg;

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
    private ProgressDialogFragment mProgressDialog;
    private EventCreatorDialogFragment mEventOrganizerDialog;
    private Handler mLoadingDataDialogHandler;

    final long SCROLL_BACK_WAIT_TIME = 10000;
    final long SHOW_LOADING_DIALOG_WAIT_TIME = 1000;

    private RoomStatusHandler mRoomStatusHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeHelper.onActivityCreateSetTheme(this);

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
        mCurrentDateTimeTv.setText(TimeHelper.getCurrentDateTime(this));
        mCalendarImg.setOnClickListener(this);
        mRoomImg.setOnClickListener(this);
        mNightModeImg.setOnClickListener(this);

        // Dialog
        mProgressDialog = ProgressDialogFragment.newInstance();
        mEventOrganizerDialog = EventCreatorDialogFragment.newInstance();
        mLoadingDataDialogHandler = new Handler();

        mRoomStatusHandler = new RoomStatusHandler(this);
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
    }

    private void setUpHorizontalTimelineView() {

        mAdapter.setOnEventClickListener(this);

        mAdapter.setOnEventRenderFinishListener(this::updateHorizontalTimeline);

        mHorizontalTimelineRv.addItemDecoration(new HorizontalEventItemDecoration(this, R.drawable.divider_horizontal_event));
        mHorizontalTimelineRv.setAdapter(mAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_calendar:
                Intent calendarIntent = new Intent(RoomStatusActivity.this, EventsCalendarActivity.class);
                Bundle args = new Bundle();
                args.putString(EventsCalendarActivity.ARG_ROOM_ID, Badoo.getCurrentRoom().getId());
                args.putBoolean(EventsCalendarActivity.ARG_SHOW_ROOM_LIST_ICON, true);
                calendarIntent.putExtras(args);
                startActivityForResult(calendarIntent, REQUEST_BOOK_ROOM);
                break;
            case R.id.img_room:
                Intent roomListIntent = new Intent(RoomStatusActivity.this, RoomListActivity.class);
                startActivityForResult(roomListIntent, REQUEST_BOOK_ROOM);
                break;

            case R.id.img_night_mode:

                if (ThemeHelper.getCurrentTheme() == ThemeHelper.THEME_DARK) {
                    ThemeHelper.changeToTheme(this, ThemeHelper.THEME_DEFAULT);
                } else {
                    ThemeHelper.changeToTheme(this, ThemeHelper.THEME_DARK);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void renderRoomEvent(EventModel currentEvent) {
        updateRoomStatusView(currentEvent);
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
    public void onCountDownTicking() {
        mPresenter.onCountDownTicking();
    }

    @Override
    public void onCountDownFinished() {
        mPresenter.onCountDownFinished();
    }

    @Override
    public void updateRoomStatusView(EventModel currentEvent) {
        mRoomStatusHandler.updateRoomStatusView(currentEvent);
    }

    @Override
    public void updateRoomStatusTextView(EventModel mCurrentEvent) {
        mRoomStatusHandler.updateRoomStatusTextView(mCurrentEvent);
    }

    @Override
    public void updateExtendButtonState(boolean state) {
        mRoomStatusHandler.updateExtendButtonState(state);
    }

    @Override
    public void renderRoomEventList(List<EventModel> roomEventModelList) {
        mAdapter.setEventList(roomEventModelList);
    }

    @Override
    public void updateHorizontalTimeline() {
        if (mAdapter.getPastTimeWidth() == -1) {
            return;
        }
        float leftMargin = -mAdapter.getPastTimeWidth()
            + getApplicationContext().getResources().getDimension(R.dimen.horizontal_timeline_current_time_mark_left_margin);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHorizontalTimelineRv.getLayoutParams();
        params.setMargins((int) leftMargin, 0 ,0 ,0);
        mHorizontalTimelineRv.setLayoutParams(params);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void bookRoomFrom(int position, ArrayList<EventModel> mEventModelList) {
        Intent intent = new Intent(this, RoomBookingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(RoomBookingActivity.ARG_POSITION, position);
        bundle.putParcelableArrayList(RoomBookingActivity.ARG_ROOM_LIST, mEventModelList);
        bundle.putString(RoomBookingActivity.ARG_ROOM_ID, Badoo.getCurrentRoom().getId());
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, REQUEST_BOOK_ROOM);
    }

    @Override
    public void onAvailableEventClick(int position, List<EventModel> eventModelList) {
        bookRoomFrom(position, (ArrayList<EventModel>) eventModelList);
    }

    @Override
    public void onBusyEventClicked(EventModel eventModel) {
        if (mEventOrganizerDialog != null) {
            mEventOrganizerDialog.setEvent(eventModel);
            mEventOrganizerDialog.show(this);
        }
    }

    @Override
    public void showEventOrganizerDialog(EventModel event) {
        if (mEventOrganizerDialog != null) {
            mEventOrganizerDialog.setEvent(event);
            mEventOrganizerDialog.show(this);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
        mCurrentDateTimeTv.setText(TimeHelper.getCurrentDateTime(this));
        mPresenter.onSystemTimeRefresh();
    }

    @Override
    protected void onCalendarUpdate() {
        super.onCalendarUpdate();
        if(mPresenter != null) {
            mPresenter.getEvents();
        }
    }
}
