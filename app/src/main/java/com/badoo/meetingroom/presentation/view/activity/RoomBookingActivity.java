package com.badoo.meetingroom.presentation.view.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.view.component.edittext.ExtendedEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomBookingActivity extends BaseActivity {

    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tv_booking_date) TextView mBookingDateTv;
    @BindView(R.id.tv_booking_period) TextView mBookingPeriodTv;
    @BindView(R.id.et_email) ExtendedEditText mEmailEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);
        ButterKnife.bind(this);

        setUpToolbar();
        setUpTextViews();
        setUpEditText();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
