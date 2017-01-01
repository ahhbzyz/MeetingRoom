package com.badoo.meetingroom.presentation.view.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.impl.RoomBookingPresenterImpl;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.component.edittext.ExtendedEditText;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomBookingActivity extends BaseActivity implements RoomBookingView{

    @Inject RoomBookingPresenterImpl mPresenter;
    @Inject TimeSlotsAdapter mAdapter;

    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tv_booking_date) TextView mBookingDateTv;
    @BindView(R.id.tv_booking_period) TextView mBookingPeriodTv;
    @BindView(R.id.et_email) ExtendedEditText mEmailEt;
    @BindView(R.id.rv_time_slots) RecyclerView mTimeSlotsRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);
        ButterKnife.bind(this);

        this.getApplicationComponent().inject(this);

        setUpToolbar();
        setUpTextViews();
        setUpEditText();
        setUpRecyclerView();

        mPresenter.setView(this);
        mPresenter.init();
    }


    private void setUpToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Typeface stolzlMedium = Typeface.createFromAsset(getAssets(),"fonts/stolzl_medium.otf");
        mRoomNameTv.setTypeface(stolzlMedium);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setUpTextViews() {
        Typeface stolzlRegular = Typeface.createFromAsset(getAssets(),"fonts/stolzl_regular.otf");
        mBookingDateTv.setTypeface(stolzlRegular);
        mBookingPeriodTv.setTypeface(stolzlRegular);
        mEmailEt.setTypeface(stolzlRegular);
    }

    private void setUpEditText() {
        mEmailEt.setOnClickListener(v -> {
            mEmailEt.setSelection(0);
        });

        mEmailEt.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mEmailEt.clearFocus();
            }
            return false;
        });
    }

    private void setUpRecyclerView() {
        mTimeSlotsRv.setLayoutManager(new LinearLayoutManager(
            context(),
            LinearLayoutManager.HORIZONTAL,
            false));
        mTimeSlotsRv.setAdapter(mAdapter);
    }

    @Override
    public void setTimeSlotsInView() {
        Bundle bundle = getIntent().getBundleExtra("timePeriod");
        if (bundle == null) {
            return;
        }
        long startTime = bundle.getLong("startTime");
        long endTime = bundle.getLong("endTime");

        if (startTime < TimeHelper.getMidNightTimeOfDay(1)) {
            mBookingDateTv.setText("Today");
        } else {
            mBookingDateTv.setText(TimeHelper.formatDate(startTime));
        }
        mBookingPeriodTv.setText(TimeHelper.formatTime(startTime) + " - " +TimeHelper.formatTime(endTime));

        mAdapter.setTimeSlots(startTime, endTime);

        // Set left padding
        if (mAdapter.getItemCount() >= 1) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int availableSlotLength = (int) ((mAdapter.getItemCount() - 1) * getResources().getDimension(R.dimen.room_booking_time_slot_width));

            int leftPadding;

            if (availableSlotLength >= width - 2 * getResources().getDimension(R.dimen.room_booking_view_margin)) {
                leftPadding = (int) getResources().getDimension(R.dimen.room_booking_view_margin);
            } else {
                leftPadding = (width - availableSlotLength) / 2;
            }
            mTimeSlotsRv.setPadding(leftPadding, 0, 0, 0);
            mAdapter.setRecyclerViewParams(width, leftPadding);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        return getApplicationContext();
    }


}
