package com.badoo.meetingroom.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.impl.RoomBookingPresenterImpl;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.component.edittext.ExtendedEditText;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomBookingActivity extends BaseActivity implements RoomBookingView{

    private static final int REQUEST_AUTHORIZATION = 1001;
    @Inject RoomBookingPresenterImpl mPresenter;
    @Inject TimeSlotsAdapter mAdapter;

    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tv_booking_date) TextView mBookingDateTv;
    @BindView(R.id.tv_booking_period) TextView mBookingPeriodTv;
    @BindView(R.id.et_email) ExtendedEditText mEmailEt;
    @BindView(R.id.rv_time_slots) RecyclerView mTimeSlotsRv;
    @BindView(R.id.btn_book) Button mBookBtn;

    private ProgressDialog mLoadingDataDialog;

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
        setUpBookButton();
        setUpLoadingDataDialog();

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

    private void setUpBookButton() {
        mBookBtn.setOnClickListener(v -> mPresenter.bookRoom(mEmailEt.getText().toString().trim()));
    }

    private void setUpLoadingDataDialog(){
        mLoadingDataDialog = new ProgressDialog(this);
        mLoadingDataDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoadingDataDialog.setMessage("Booking room...");
        mLoadingDataDialog.setCanceledOnTouchOutside(false);
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

        if (TimeHelper.isMidNight(endTime)) {
           // mBookingPeriodTv.setText(TimeHelper.formatTime(startTime) + " - " + "24:00");
        } else {

        }
        mBookingPeriodTv.setText("No slots selected");
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

        mAdapter.setOnItemClickListener(timeSlotList -> {
            mPresenter.setTimeSlotList(timeSlotList);
        });
    }

    @Override
    public void updateTimePeriodTextView(long startTime, long endTime) {
        if (startTime != -1 && endTime != -1) {
            if (TimeHelper.isMidNight(endTime)) {
                mBookingPeriodTv.setText(TimeHelper.formatTime(startTime) + " - " + "24:00");
            } else {
                mBookingPeriodTv.setText(TimeHelper.formatTime(startTime) + " - " +TimeHelper.formatTime(endTime));
            }
        } else {
            mBookingPeriodTv.setText("No Slots Selected");
        }
    }

    @Override
    public void showRecoverableAuth(UserRecoverableAuthIOException e) {
        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    @Override
    public void showBookingSuccessful() {
        Toast.makeText(this, "Room is booked successfully", Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(0, 0);
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
        if (visibility) {
            mLoadingDataDialog.show();
        } else {
            mLoadingDataDialog.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void showRetryLoading(boolean visibility) {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.bookRoom(mEmailEt.getText().toString().trim());
                }
                break;
        }
    }

    @Override
    public Context context() {
        return getApplicationContext();
    }
}
