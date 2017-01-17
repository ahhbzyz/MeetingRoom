package com.badoo.meetingroom.presentation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.domain.interactor.GetAvatar;
import com.badoo.meetingroom.presentation.model.BadooPersonModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.adapter.EmailAutoCompleteAdapter;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.component.autocompletetextview.MyAutoCompleteTextView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomBookingActivity extends BaseActivity implements RoomBookingView {

    private static final int REQUEST_AUTHORIZATION = 1001;

    @Inject RoomBookingPresenter mPresenter;
    @Inject TimeSlotsAdapter mAdapter;
    @Inject @Named("stolzl_regular") Typeface mStolzlRegularTypeface;
    @Inject @Named("stolzl_medium") Typeface mStolzlMediumTypeface;
    @Inject @Named(GetAvatar.NAME) GetAvatar mGetAvatarUseCase;

    @BindView(R.id.tv_current_date) TextView mCurrentDateTv;
    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tv_booking_date) TextView mBookingDateTv;
    @BindView(R.id.tv_booking_period) TextView mBookingPeriodTv;

    @BindView(R.id.autocomplete_email_address) MyAutoCompleteTextView mAutoCompleteTv;
    @BindView(R.id.rv_time_slots) RecyclerView mTimeSlotsRv;
    @BindView(R.id.btn_book) Button mBookBtn;
    @BindView(R.id.pb_loading_contacts) ProgressBar mLoadingContactsPb;

    private boolean hasSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);
        ButterKnife.bind(this);
        this.getComponent().inject(this);
        mPresenter.setView(this);

        initViews();

        mPresenter.init();
    }


    private void initViews() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mRoomNameTv.setTypeface(mStolzlMediumTypeface);
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(RoomBookingActivity.this));
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mBookingDateTv.setTypeface(mStolzlRegularTypeface);
        mBookingPeriodTv.setTypeface(mStolzlRegularTypeface);

        mBookBtn.setOnClickListener(v -> mPresenter.bookRoom(mAutoCompleteTv.getText().toString().trim()));
        mBookBtn.setClickable(false);
        mBookBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_book_disabled));



        mTimeSlotsRv.setLayoutManager(new LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false));
        mTimeSlotsRv.setAdapter(mAdapter);

        mAutoCompleteTv.setTypeface(mStolzlRegularTypeface);
    }

    @Override
    public void setUpAutoCompleteTextView(List<BadooPersonModel> badooPersonModelList) {

        EmailAutoCompleteAdapter adapter = new EmailAutoCompleteAdapter(this, R.layout.item_badoo_person, badooPersonModelList);
        mAutoCompleteTv.setAdapter(adapter);
        mAutoCompleteTv.setThreshold(getString(R.string.badoo_mail_suffix).length() + 2);
        mAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateBookingParameters();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    s.append(getString(R.string.badoo_mail_suffix));
                }
            }
        });

        mAutoCompleteTv.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                RoomBookingActivity.this.setImmersiveMode();
            } else {
            }
        });
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
            mBookingDateTv.setText(getString(R.string.today));
        } else {
            mBookingDateTv.setText(TimeHelper.formatDate(startTime));
        }

        mAdapter.setTimeSlots(startTime, endTime);

        // Set left padding
        if (mAdapter.getItemCount() >= 1) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int availableSlotLength = (int) ((mAdapter.getItemCount() - 1) * getResources().getDimension(R.dimen.room_booking_time_slot_width));

            int leftPadding;

            if (availableSlotLength >= width - 2 * getResources().getDimension(R.dimen.room_booking_view_margin)) {
                leftPadding = (int) getResources().getDimension(R.dimen.room_booking_view_margin);
            }
            else {
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
                mBookingPeriodTv.setText(TimeHelper.formatTime(startTime) + " - " + TimeHelper.formatTime(endTime));
            }
            hasSlots = true;
        }
        else {
            mBookingPeriodTv.setText(getString(R.string.no_slots_selected_alert));
            hasSlots = false;
        }

        validateBookingParameters();
    }

    private void validateBookingParameters() {
        if (hasSlots && isValidEmailAddress(mAutoCompleteTv.getText().toString().trim())) {
            mBookBtn.setClickable(true);
            mBookBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_book));
        }
        else {
            mBookBtn.setClickable(false);
            mBookBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_book_disabled));
        }
    }

    @Override
    public void showRecoverableAuth(UserRecoverableAuthIOException e) {
        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    @Override
    public void showBookingSuccessful(long value) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("startTime", value);
        setResult(Activity.RESULT_OK, returnIntent);
        finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoadingData(String message) {
        mLoadingContactsPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingData() {
        mLoadingContactsPb.setVisibility(View.GONE);
    }


    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.bookRoom(mAutoCompleteTv.getText().toString().trim());
                }
                break;
        }
    }


    @Override
    public Context context() {
        return getApplicationContext();
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    protected void onSystemTimeRefresh() {
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(RoomBookingActivity.this));

    }

}
