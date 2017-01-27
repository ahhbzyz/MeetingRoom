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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.adapter.EmailAutoCompleteAdapter;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.component.autocompletetextview.MyAutoCompleteTextView;
import com.badoo.meetingroom.presentation.view.component.layoutmanager.LinearLayoutManagerWithSmoothScroller;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomBookingActivity extends BaseActivity implements RoomBookingView {

    @Inject
    RoomBookingPresenter mPresenter;
    @Inject
    TimeSlotsAdapter mAdapter;
    @Inject
    @Named("stolzl_regular")
    Typeface mStolzlRegularTypeface;
    @Inject
    @Named("stolzl_medium")
    Typeface mStolzlMediumTypeface;

    @BindView(R.id.tv_current_date_time)
    TextView mCurrentDateTimeTv;
    @BindView(R.id.tv_room_name)
    TextView mRoomNameTv;
    @BindView(R.id.tv_booking_date)
    TextView mBookingDateTv;
    @BindView(R.id.tv_booking_period)
    TextView mBookingPeriodTv;

    @BindView(R.id.autocomplete_email_address)
    MyAutoCompleteTextView mAutoCompleteTv;
    @BindView(R.id.rv_time_slots)
    RecyclerView mTimeSlotsRv;
    @BindView(R.id.btn_book)
    Button mBookBtn;

    private boolean hasSlots;
    private String mSelectedEmail;
    private String mRoomId;
    LinearLayoutManagerWithSmoothScroller mLayoutManager;

    public static final String ARG_ROOM_LIST = "roomList";
    public static final String ARG_POSITION = "position";
    public static final String ARG_ROOM_ID = "roomId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);
        ButterKnife.bind(this);
        this.getComponent().inject(this);
        mPresenter.setView(this);
        initViews();
        getBundleData();
    }


    private void initViews() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mRoomNameTv.setTypeface(mStolzlMediumTypeface);
        mCurrentDateTimeTv.setText(TimeHelper.getCurrentDateTime(this));
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mBookingDateTv.setTypeface(mStolzlRegularTypeface);
        mBookingPeriodTv.setTypeface(mStolzlRegularTypeface);

        mBookBtn.setOnClickListener(v -> mPresenter.bookRoom(mSelectedEmail, mRoomId));
        mBookBtn.setClickable(false);
        mBookBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_book_disabled));


        mLayoutManager = new LinearLayoutManagerWithSmoothScroller(context(), LinearLayoutManager.HORIZONTAL, false);
        mTimeSlotsRv.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(context(), LinearLayoutManager.HORIZONTAL, false));
        mTimeSlotsRv.setAdapter(mAdapter);

        mAutoCompleteTv.setTypeface(mStolzlRegularTypeface);
    }

    @Override
    public void getBundleData() {
        ArrayList<EventModel> eventModelList = getIntent().getExtras().getParcelableArrayList(ARG_ROOM_LIST);
        int position = getIntent().getExtras().getInt(ARG_POSITION);
        mRoomId = getIntent().getExtras().getString(ARG_ROOM_ID);
        mPresenter.setTimeSlotList(position, eventModelList);
        mPresenter.getContactList();
    }

    @Override
    public void setUpAutoCompleteTextView(List<PersonModel> personModelList) {

        EmailAutoCompleteAdapter adapter = new EmailAutoCompleteAdapter(this, R.layout.item_badoo_person, personModelList);
        mAutoCompleteTv.setEnabled(true);
        mAutoCompleteTv.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mAutoCompleteTv.setAdapter(adapter);
        mAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSelectedEmail = null;
                validateBookingParameters();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAutoCompleteTv.setOnEditorActionListener((v, actionId, event) -> {

            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (mSelectedEmail != null) {
                    mPresenter.bookRoom(mSelectedEmail, mRoomId);
                }
            }
            return false;
        });

        mAutoCompleteTv.setOnItemClickListener((parent, view, position, id) -> {
            mSelectedEmail = personModelList.get(position).getEmailAddress();
            validateBookingParameters();
        });

        mAutoCompleteTv.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                RoomBookingActivity.this.setImmersiveMode();
            }
        });
    }

    @Override
    public void renderTimeSlotsInView(int position, List<EventModel> availableEventList, String date) {

        mBookingDateTv.setText(date);

        // Set left padding
        mAdapter.setEventModelList(availableEventList);

        if (mAdapter.getItemCount() >= 1) {

            int width = Resources.getSystem().getDisplayMetrics().widthPixels;

            float leftPadding = (width) / 2 - getResources().getDimension(R.dimen.room_booking_time_slot_width) / 2f;
            mTimeSlotsRv.setPadding((int) leftPadding, 0, 0, 0);
        }

        mTimeSlotsRv.scrollToPosition(position);

        mAdapter.setOnItemClickListener((startIndex, endIndex) -> mPresenter.updateSelectedTimePeriod(startIndex, endIndex));
    }

    @Override
    public void updateTimePeriodTextView(long startTime, long endTime) {
        if (startTime != -1 && endTime != -1) {
            if (TimeHelper.isMidNight(endTime)) {
                mBookingPeriodTv.setText(TimeHelper.formatTime(startTime) + " - " + "24:00");
            }
            else {
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
        if (hasSlots && isValidEmailAddress(mSelectedEmail)) {
            mBookBtn.setClickable(true);
            mBookBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_book));
        }
        else {
            mBookBtn.setClickable(false);
            mBookBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_book_disabled));
        }
    }

    @Override
    public void showBookingSuccessful(long value) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoadingData(String message) {

    }

    @Override
    public void dismissLoadingData() {
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return getApplicationContext();
    }

    private boolean isValidEmailAddress(String email) {
        if(email == null) return false;
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    protected void onSystemTimeRefresh() {
        mCurrentDateTimeTv.setText(TimeHelper.getCurrentDateTime(this));
    }

}
