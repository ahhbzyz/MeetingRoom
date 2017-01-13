package com.badoo.meetingroom.presentation.view.activity;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.view.component.button.LongPressButton;
import com.badoo.meetingroom.presentation.view.component.button.TwoLineTextButton;
import com.badoo.meetingroom.presentation.view.viewutils.ViewHelper;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

class RoomStatusHandler {

    private final RoomStatusActivity activity;
    private int mCircleBtnDiameter;
    private int mCircleBtnLeftMargin;
    private int mCircleBtnNameTopMargin;

    RoomStatusHandler(RoomStatusActivity activity) {
        this.activity = activity;
        mCircleBtnDiameter = (int) activity.getResources().getDimension(R.dimen.circle_button_diameter);
        mCircleBtnLeftMargin = (int) activity.getResources().getDimension(R.dimen.circle_button_left_margin);
        mCircleBtnNameTopMargin = (int) activity.getResources().getDimension(R.dimen.circle_button_name_top_margin);
    }

    void showButtonGroupForAvailableStatus() {

        activity.mFastBookTv.setVisibility(View.VISIBLE);

        // Update button group layout
        activity.mButtonsLayout.removeAllViews();
        activity.mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        activity.mButtonsLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        // Circle time view button
        int btnImgWidth = (int) activity.getResources().getDimension(R.dimen.circle_time_view_btn_available_img_width);
        int btnImgHeight = (int) activity.getResources().getDimension(R.dimen.circle_time_view_btn_available_img_height);
        activity. mCircleTimeViewBtn.setVisibility(View.VISIBLE);
        activity. mCircleTimeViewBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_add_black, btnImgWidth, btnImgHeight));

        TwoLineTextButton[] buttons = new TwoLineTextButton[3];
        final int min = 5;
        for (int i = 0; i < 3; i++) {
            buttons[i] = new TwoLineTextButton(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter);
            buttons[i].setLayoutParams(params);
            if (i == 1) {
                params.setMargins(mCircleBtnLeftMargin, 0, mCircleBtnLeftMargin, 0);
                buttons[1].setLayoutParams(params);
            }
            buttons[i].setTopText(min * (i + 1) + "");
            buttons[i].setBottomText("min");
            buttons[i].setBackground(ContextCompat.getDrawable(activity, R.drawable.btn_circle_time));
            activity.mButtonsLayout.addView(buttons[i]);
            final int temp = i;
            buttons[i].setOnClickListener(v -> activity.mPresenter.insertEvent(min * (temp + 1)));
        }
    }


    void showButtonGroupForOnHoldStatus() {

        activity.mFastBookTv.setVisibility(View.GONE);

        // Update button group layout
        activity.mButtonsLayout.removeAllViews();
        activity.mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        activity.mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        activity.mCircleTimeViewBtn.setVisibility(View.INVISIBLE);

        // Confirm button
        ImageButton mConfirmBtn = new ImageButton(activity);
        mConfirmBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mConfirmBtn.setScaleType(ImageView.ScaleType.CENTER);
        mConfirmBtn.setBackground(ContextCompat.getDrawable(activity, R.drawable.btn_oval_confirm));
        int confirmBtnImgWidth = (int) activity.getResources().getDimension(R.dimen.confirm_btn_img_width);
        int confirmBtnImgHeight = (int) activity.getResources().getDimension(R.dimen.confirm_btn_img_height);
        mConfirmBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_done_white, confirmBtnImgWidth, confirmBtnImgHeight));
        LinearLayout mConfirmBtnWithText = ViewHelper.addTextUnderBtn(activity, mConfirmBtn, activity.getString(R.string.confirm), mCircleBtnNameTopMargin);
        activity.mButtonsLayout.addView(mConfirmBtnWithText);
        mConfirmBtn.setOnClickListener(v -> activity.mPresenter.setEventConfirmed());

        // Fake button
        ImageButton mFakeBtn = new ImageButton(activity);
        mFakeBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(mCircleBtnLeftMargin, 0, mCircleBtnLeftMargin, 0);
        mFakeBtn.setLayoutParams(params);
        mFakeBtn.setVisibility(View.INVISIBLE);
        activity.mButtonsLayout.addView(mFakeBtn);

        // Dismiss button
        ImageButton mDismissBtn = new ImageButton(activity);
        mDismissBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mDismissBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDismissBtn.setBackground(ContextCompat.getDrawable(activity, R.drawable.btn_circle_dismiss));
        int dismissBtnImgWidth = (int) activity.getResources().getDimension(R.dimen.dismiss_btn_img_width);
        int dismissBtnImgHeight = (int) activity.getResources().getDimension(R.dimen.dismiss_btn_img_height);
        mDismissBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_clear_black, dismissBtnImgWidth, dismissBtnImgHeight));
        LinearLayout mDismissBtnWithText = ViewHelper.addTextUnderBtn(activity, mDismissBtn, activity.getString(R.string.dismiss), mCircleBtnNameTopMargin);
        activity.mButtonsLayout.addView(mDismissBtnWithText);
        mDismissBtn.setOnClickListener(v -> activity.mPresenter.deleteEvent());
    }

    void showButtonGroupForBusyStatus() {

        activity.mFastBookTv.setVisibility(View.GONE);

        // Update button group layout
        activity.mButtonsLayout.removeAllViews();
        activity.mButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        activity.mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        int ctvBtnImgWidth = (int) activity.getResources().getDimension(R.dimen.circle_time_view_btn_busy_img_width);
        int ctvBtnImgHeight = (int) activity.getResources().getDimension(R.dimen.circle_time_view_btn_busy_img_height);
        activity.mCircleTimeViewBtn.setVisibility(View.VISIBLE);
        activity.mCircleTimeViewBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_info_black, ctvBtnImgWidth, ctvBtnImgHeight));

        // Hold to end btn
        LongPressButton mEndBtn = new LongPressButton(activity);
        int diameter = (int) (mCircleBtnDiameter + mEndBtn.getCountDownCircleWidth() * 4);
        mEndBtn.setLayoutParams(new LinearLayout.LayoutParams(diameter, diameter));
        mEndBtn.setScaleType(ImageView.ScaleType.CENTER);
        int endBtnImgWidth = (int) activity.getResources().getDimension(R.dimen.end_btn_img_width);
        int endBtnImgHeight = (int) activity.getResources().getDimension(R.dimen.end_btn_img_height);
        mEndBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_clear_white, endBtnImgWidth, endBtnImgHeight));
        LinearLayout mEndBtnWithText = ViewHelper.addTextUnderBtn(activity, mEndBtn, activity.getString(R.string.hold_to_end), (int) (mCircleBtnNameTopMargin - 2 * mEndBtn.getCountDownCircleWidth()));
        mEndBtnWithText.getChildAt(mEndBtnWithText.getChildCount() - 1).setPadding(0, 0, 0, (int) (2 * mEndBtn.getCountDownCircleWidth()));
        activity.mButtonsLayout.addView(mEndBtnWithText);
        mEndBtn.setOnCountDownListener(() -> activity.mPresenter.deleteEvent());

        // Do not disturb btn
        ImageButton mDNDBtn = new ImageButton(activity);
        mDNDBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mDNDBtn.setScaleType(ImageView.ScaleType.CENTER);
        mDNDBtn.setBackground(ContextCompat.getDrawable(activity, R.drawable.btn_circle_busy));
        int dndBtnImgWidth = (int) activity.getResources().getDimension(R.dimen.dnd_btn_img_width);
        int dndBtnImgHeight = (int) activity.getResources().getDimension(R.dimen.dnd_btn_img_height);
        mDNDBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_donotdisturb_black, dndBtnImgWidth, dndBtnImgHeight));
        LinearLayout mDNDBtnWithText = ViewHelper.addTextUnderBtn(activity, mDNDBtn, activity.getString(R.string.do_not_disturb), mCircleBtnNameTopMargin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(mCircleBtnLeftMargin, 0, mCircleBtnLeftMargin, 0);
        mDNDBtnWithText.setLayoutParams(params);
        activity.mButtonsLayout.addView(mDNDBtnWithText);
        mDNDBtn.setOnClickListener(v -> activity.mPresenter.setDoNotDisturb(true));

        ImageButton mExtendBtn = new ImageButton(activity);
        mExtendBtn.setLayoutParams(new LinearLayout.LayoutParams(mCircleBtnDiameter, mCircleBtnDiameter));
        mExtendBtn.setScaleType(ImageView.ScaleType.CENTER);
        mExtendBtn.setBackground(ContextCompat.getDrawable(activity, R.drawable.btn_circle_busy));
        int extendBtnImgWidth = (int) activity.getResources().getDimension(R.dimen.extend_btn_img_width);
        int extendBtnImgHeight = (int) activity.getResources().getDimension(R.dimen.extend_btn_img_height);
        mExtendBtn.setImageDrawable(ViewHelper.createScaleDrawable(activity, R.drawable.ic_add_black, extendBtnImgWidth, extendBtnImgHeight));
        LinearLayout mExtendBtnWithText = ViewHelper.addTextUnderBtn(activity, mExtendBtn, activity.getString(R.string.extend), mCircleBtnNameTopMargin);
        activity.mButtonsLayout.addView(mExtendBtnWithText);
        mExtendBtn.setOnClickListener(v -> activity.mPresenter.updateEvent());
    }

    void showButtonGroupForDoNotDisturbStatus(String eventEndTime) {
        activity.mFastBookTv.setVisibility(View.GONE);

        activity.mButtonsLayout.removeAllViews();
        activity.mButtonsLayout.setOrientation(LinearLayout.VERTICAL);
        activity.mButtonsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        activity.mCircleTimeViewBtn.setVisibility(View.INVISIBLE);

        TextView busyUntilTv = new TextView(activity);
        busyUntilTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        busyUntilTv.setText(activity.getString(R.string.busy_until_upper_case));
        busyUntilTv.setTextSize(activity.getResources().getDimension(R.dimen.dnd_status_busy_until_text_size));
        busyUntilTv.setTextColor(Color.BLACK);
        busyUntilTv.setTypeface(activity.mStolzlRegularTypeface);
        busyUntilTv.setIncludeFontPadding(false);
        activity.mButtonsLayout.addView(busyUntilTv);

        TextView mEventEndTimeTv = new TextView(activity);
        mEventEndTimeTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mEventEndTimeTv.setText(eventEndTime);
        mEventEndTimeTv.setTextColor(Color.BLACK);
        mEventEndTimeTv.setTextSize(activity.getResources().getDimension(R.dimen.dnd_status_current_time_text_size));
        mEventEndTimeTv.setIncludeFontPadding(false);
        mEventEndTimeTv.setTypeface(activity.mStolzMediumTypeface);
        activity.mButtonsLayout.addView(mEventEndTimeTv);
    }
}
