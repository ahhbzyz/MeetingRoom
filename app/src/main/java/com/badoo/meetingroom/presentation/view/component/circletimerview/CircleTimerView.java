package com.badoo.meetingroom.presentation.view.component.circletimerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.concurrent.TimeUnit;


/**
 * Created by zhangyaozhong on 06/12/2016.
 */

public class CircleTimerView extends View {

    /**
     * Circle timer progress
     */
    private float mMaxProgress = 100.0f;
    private float mCurrentProgress = 0.0f;

    /**
     * Left count down time
     */
    private long mLeftCountDownTime = 0;

    /**
     * Default attributes
     */
    // Circle attributes
    private final float DEFAULT_BG_CIRCLE_RADIUS = dp2px(100);
    private final float DEFAULT_BG_CIRCLE_STROKE_WIDTH = dp2px(32);
    private final int DEFAULT_BG_CIRCLE_COLOR = Color.parseColor("#69E27E");
    private final int DEFAULT_ARC_COLOR = Color.parseColor("#69E27E");

    // Timer texts
    private final float DEFAULT_TIMER_INFO_TEXT_SIZE = sp2px(24);
    private final float DEFAULT_TIMER_TIME_TEXT_SIZE = sp2px(72);
    private final int DEFAULT_TIMER_INFO_TEXT_WHITE_COLOR = Color.parseColor("#FFFFFF");
    private final int DEFAULT_TIMER_INFO_TEXT_BLACK_COLOR = Color.parseColor("#212123");

    private final int DEFAULT_TIMER_TIME_TEXT_COLOR = Color.parseColor("#212123");

    private final String DEFAULT_TIMER_TIME_TEXT = "00:00";
    private final String DEFAULT_TIMER_INFO_TEXT = "Circle Timer";
    private final String MAX_TIME_SHOWN = "2H+";

    // Circle button
    private final int DEFAULT_CIRCLE_BTN_COLOR = Color.parseColor("#F1F1F1");
    private final float DEFAULT_CIRCLE_BTN_RADIUS = dp2px(20);

    /**
     * For save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";

    // Circle
    private static final String INSTANCE_BG_CIRCLE_COLOR = "bg_circle_color";
    private static final String INSTANCE_ARC_COLOR = "arc_color";
    private static final String INSTANCE_BG_CIRCLE_STROKE_WIDTH = "bg_circle_stroke_width";

    // Timer information
    private static final String INSTANCE_TIMER_INFO_TEXT_COLOR = "timer_info_text_color";
    private static final String INSTANCE_TIMER_INFO_TEXT_SIZE = "timer_info_text_size";
    private static final String INSTANCE_TIMER_INFO_TEXT_PADDING = "timer_info_text_padding";
    private static final String INSTANCE_TIMER_INFO_TEXT = "timer_info_text";

    // Timer time
    private static final String INSTANCE_TIMER_TIME_TEXT_COLOR = "timer_time_text_color";
    private static final String INSTANCE_TIMER_TIME_TEXT_SIZE = "timer_time_text_size";
    private static final String INSTANCE_TIMER_TIME_TEXT = "timer_time_text";

    // Tail icon
    private static final String INSTANCE_TAIL_ICON_DRAWABLE_ID = "tail_icon_drawable_id";
    private static final String INSTANCE_TAIL_ICON_VISIBILITY = "tail_icon_visibility";
    private static final String INSTANCE_TAIL_ICON_PADDING = "tail_icon_padding";

    // Circle btn
    private static final String INSTANCE_CIRCLE_BTN_ICON_DRAWABLE_ID = "circle_btn_icon_drawable_id";
    private static final String INSTANCE_CIRCLE_BTN_ICON_VISIBILITY = "circle_btn_icon_visibility";

    // Alert icon
    private static final String INSTANCE_ALERT_ICON_DRAWABLE_ID = "alert_icon_drawable_id";
    private static final String INSTANCE_ALERT_ICON_VISIBILITY = "alert_icon_visibility";

    // Progress
    private static final String INSTANCE_CURRENT_PROGRESS = "current_progress";
    private static final String INSTANCE_MAX_PROGRESS = "max_progress";

    // Count down time
    private static final String INSTANCE_LEFT_COUNT_DOWN_TIME = "left_countdown_time";

    // Is timer running
    private static final String INSTANCE_IS_TIMER_RUNNING = "is_timer_running";

    // Current status
    private static final String INSTANCE_CURR_STATUS = "curr_status";


    /**
     * Circle Timer status
     */
    private RoomEventModel mCurrRoomEvent;

    /**
     * Circle attributes
     */
    private float mBgCircleCx;
    private float mBgCircleCy;
    private float mBgCircleRadius;
    private float mBgCircleStrokeWidth;

    /**
     * Circle degree
     */
    private float startDegree = 270f;
    private float rotateDegree = 360f;

    /**
     * Circle Paint
     */
    private Paint mBgCirclePaint;
    private Paint mArcPaint;
    private Paint mOvalPaint;

    /**
     * Start and end oval of arc
     */
    private RectF mArcOval;

    /**
     * Circle Color
     */
    private int mBgCircleColor;
    private int mArcColor;

    /**
     * Timer information and text
     */
    private String mTimerTimeText;
    private String mTimerInfoText;

    private boolean mTimerTimeVisibility;

    /**
     * Text Paint
     */
    private Paint mTimerInfoTextPaint;
    private Paint mTimerTimeTextPaint;

    /**
     * Text Color
     */
    private int mTimerInfoTextColor;
    private int mTimerTimeTextColor;

    /**
     * Text Size
     */
    private float mTimerInfoTextSize;
    private float mTimerTimeTextSize;

    /**
     * Timer information padding
     */
    private float mTimerInfoTextPadding = 30f;

    /**
     * Timer time text bounds for centering
     */
    private Rect mTimerTimeTextBounds;

    /**
     * Tail Icon id
     */
    private int mTailIconDrawableId;

    /**
     * Tail Icon Drawable
     */
    private Drawable mTailIconDrawable;

    /**
     * Tail Icon padding
     */
    private float mTailIconPadding = 0f;

    /**
     * Show tail Icon or not
     */
    private boolean mTailIconVisibility = false;

    /**
     * Alert icon
     */
    private int mAlertIconDrawableId;
    private Drawable mAlertIconDrawable;
    private boolean mAlertIconVisibility;


    private OnBtnClickListener mCircleBtnClickListener;

    /**
     * Count down listener
     */
    private OnCountDownListener mCountDownListener;

    /**
     * Count down timer
     */
    private CountDownTimer mCountDownTimer;

    /**
     * Is count down timer running
     */
    private boolean isTimerRunning = false;

    /**
     * Circle button
     */
    private float mCircleBtnCx;
    private float mCircleBtnCy;
    private float mCircleBtnRadius;
    private Paint mCircleBtnPaint;

    private int mCircleBtnColor;
    private boolean mCircleBtnVisibility = true;
    private Drawable mCircleBtnIconDrawable;
    private int mCircleBtnIconDrawableId;

    /**
     * Touch event
     */
    private float touchX = 0;
    private float touchY = 0;

    private ValueAnimator mAnimator;

    public CircleTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleTimerView,
                0, 0
        );

        try {
            // Background Circle
            mBgCircleRadius = ta.getFloat(R.styleable.CircleTimerView_bgCircleRadius, DEFAULT_BG_CIRCLE_RADIUS);
            mBgCircleStrokeWidth = ta.getFloat(R.styleable.CircleTimerView_bgCircleStrokeWidth, DEFAULT_BG_CIRCLE_STROKE_WIDTH);
            mBgCircleColor = ta.getColor(R.styleable.CircleTimerView_bgCircleColor, DEFAULT_BG_CIRCLE_COLOR);

            // Arc color
            mArcColor = ta.getColor(R.styleable.CircleTimerView_bgArcColor, DEFAULT_ARC_COLOR);

            // timer info
            mTimerInfoText = ta.getString(R.styleable.CircleTimerView_timerInfoText);
            mTimerInfoTextColor = ta.getColor(R.styleable.CircleTimerView_timerInfoTextColor, DEFAULT_TIMER_INFO_TEXT_BLACK_COLOR);
            mTimerInfoTextSize = ta.getFloat(R.styleable.CircleTimerView_timerInfoTextSize, DEFAULT_TIMER_INFO_TEXT_SIZE);

            // timer text
            mTimerTimeTextColor = ta.getColor(R.styleable.CircleTimerView_timerTimeTextColor, DEFAULT_TIMER_TIME_TEXT_COLOR);
            mTimerTimeTextSize = ta.getFloat(R.styleable.CircleTimerView_timerTimeTextSize, DEFAULT_TIMER_TIME_TEXT_SIZE);

            // Circle button
            mCircleBtnColor = ta.getColor(R.styleable.CircleTimerView_circleBtnColor, DEFAULT_CIRCLE_BTN_COLOR);
            mCircleBtnRadius = ta.getFloat(R.styleable.CircleTimerView_circleBtnRadius, DEFAULT_CIRCLE_BTN_RADIUS);

        } finally {
            ta.recycle();
        }

        setProgress(0f);
        setMaxProgress(100f);
        setTimerTimeVisibility(true);
        init();
    }

    /**
     * Initial paints
     */
    private void init() {

        // Bg circle paint
        mBgCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgCirclePaint.setStyle(Paint.Style.STROKE);
        mBgCirclePaint.setStrokeWidth(mBgCircleStrokeWidth);
        mBgCirclePaint.setColor(mBgCircleColor);

        // Arc paint
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mBgCircleStrokeWidth);
        mArcPaint.setColor(mArcColor);
        mArcOval = new RectF();

        // Oval paint
        mOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOvalPaint.setColor(mArcColor);

        // Timer info paint
        if (mTimerInfoText == null) {
            mTimerInfoText = DEFAULT_TIMER_INFO_TEXT;
        }
        mTimerInfoTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerInfoTextPaint.setColor(mTimerInfoTextColor);
        mTimerInfoTextPaint.setTextSize(mTimerInfoTextSize);
        mTimerInfoTextPaint.setTextAlign(Paint.Align.CENTER);
        Typeface stolzlRegularFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/stolzl_regular.otf");
        mTimerInfoTextPaint.setTypeface(stolzlRegularFont);

        // Timer text paint
        if (mTimerTimeText == null) {
            mTimerTimeText = DEFAULT_TIMER_TIME_TEXT;
        }
        mTimerTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerTimeTextPaint.setColor(mTimerTimeTextColor);
        mTimerTimeTextPaint.setTextSize(mTimerTimeTextSize);
        mTimerTimeTextPaint.setTextAlign(Paint.Align.CENTER);
        Typeface stolzlMediumFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/stolzl_medium.otf");
        mTimerTimeTextPaint.setTypeface(stolzlMediumFont);
        mTimerTimeTextBounds = new Rect();

        // Circle button
        mCircleBtnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleBtnPaint.setColor(mCircleBtnColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw bg circle
        canvas.drawCircle(mBgCircleCx, mBgCircleCy, mBgCircleRadius, mBgCirclePaint);

        // draw static oval
        canvas.drawCircle(mBgCircleCx, mBgCircleCy - (mBgCircleRadius), mBgCircleStrokeWidth / 2f, mOvalPaint);

        // draw dynamic oval
        float ovalX = mBgCircleCx + mBgCircleRadius * (float) Math.cos((rotateDegree - 90f) * Math.PI / 180f);
        float ovalY = mBgCircleCy + mBgCircleRadius * (float) Math.sin((rotateDegree - 90f) * Math.PI / 180f);
        canvas.drawCircle(ovalX, ovalY, mBgCircleStrokeWidth / 2f, mOvalPaint);

        // draw arc
        mArcOval.set(mBgCircleCx - mBgCircleRadius, mBgCircleCy - mBgCircleRadius, mBgCircleCx + mBgCircleRadius, mBgCircleCy + mBgCircleRadius);
        canvas.drawArc(mArcOval, startDegree, rotateDegree, false, mArcPaint);

        // draw tail icon
        if (mTailIconVisibility && mTailIconDrawable != null) {
            float iconX = mBgCircleCx + mBgCircleRadius * (float) Math.cos((rotateDegree - 90f - mTailIconPadding) * Math.PI / 180f);
            float iconY = mBgCircleCy + mBgCircleRadius * (float) Math.sin((rotateDegree - 90f - mTailIconPadding) * Math.PI / 180f);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            // Rotate tail icon based on current rotated degree
            canvas.rotate(rotateDegree - 360f, iconX, iconY);
            mTailIconDrawable.setBounds((int) (iconX - mBgCircleStrokeWidth / 2f), (int) (iconY - mBgCircleStrokeWidth / 2f),
                    (int) (iconX + mBgCircleStrokeWidth / 2f), (int) (iconY + mBgCircleStrokeWidth / 2f));
            mTailIconDrawable.draw(canvas);
            canvas.restore();
        }

        // Get half height of timer time
        if (mTimerTimeText != null) {
            mTimerTimeTextPaint.getTextBounds(mTimerTimeText, 0, mTimerTimeText.length(), mTimerTimeTextBounds);
            float halfTimeHeight = mTimerTimeTextBounds.height() / 2f;

            // Draw timer info
            if (mCurrRoomEvent!= null && !mCurrRoomEvent.isDoNotDisturb()) {
                canvas.drawText(mTimerInfoText, mBgCircleCx, mBgCircleCy - (mTimerInfoTextPadding + halfTimeHeight), mTimerInfoTextPaint);
            } else {
                canvas.drawText("DO NOT", mBgCircleCx, mBgCircleCy + (mBgCircleRadius / 4f), mTimerInfoTextPaint);
                canvas.drawText("DISTURB", mBgCircleCx, mBgCircleCy + (mBgCircleRadius / 2f), mTimerInfoTextPaint);
            }
        }
        // Center timer time
        if (mTimerTimeVisibility && mTimerTimeText != null) {
            float textY = (canvas.getHeight() / 2f - ((mTimerTimeTextPaint.descent() + mTimerTimeTextPaint.ascent()) / 2f));
            canvas.drawText(mTimerTimeText, mBgCircleCx, textY, mTimerTimeTextPaint);
            //float textWidth = mTimerTimeTextPaint.measureText("00:00", 0, "00:00".length());
        }

        // Set coordinates of circle button
        mCircleBtnCx = mBgCircleCx;
        mCircleBtnCy = mBgCircleCy + mBgCircleRadius / 2f;

        // Draw circle button
        if (mCircleBtnVisibility && mCircleBtnIconDrawable != null) {
            canvas.drawCircle(mCircleBtnCx, mCircleBtnCy, mCircleBtnRadius, mCircleBtnPaint);

            // Draw icon on circle btn
            if (mCircleBtnIconDrawable != null) {
                mCircleBtnIconDrawable.setBounds((int) (mCircleBtnCx - mCircleBtnRadius / 2f), (int) (mCircleBtnCy - mCircleBtnRadius / 2f),
                    (int) (mCircleBtnCx + mCircleBtnRadius / 2f), (int) (mCircleBtnCy + mCircleBtnRadius / 2f));
                mCircleBtnIconDrawable.draw(canvas);
            }
        }

        // Draw alert icon
        if (mAlertIconVisibility && mAlertIconDrawable != null) {
            float originalWidth = mAlertIconDrawable.getIntrinsicWidth();
            float originalHeight = mAlertIconDrawable.getIntrinsicHeight();

            float ratio = originalWidth / originalHeight;

            float iconWidth = mBgCircleRadius / 2f;
            float iconHeight = iconWidth / ratio;

            mAlertIconDrawable.setBounds((int) (mBgCircleCx - iconWidth / 2f), (int) (mBgCircleCy - iconHeight),
                (int) (mBgCircleCx + iconWidth / 2f), (int) (mBgCircleCy));
            mAlertIconDrawable.draw(canvas);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 2 * (int) mBgCircleRadius + (int) mBgCircleStrokeWidth;
        int desiredHeight = 2 * (int) mBgCircleRadius + (int) mBgCircleStrokeWidth;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        // Update radius
        mBgCircleRadius = Math.min(width, height) / 2f - mBgCircleStrokeWidth / 2f;


        // Set coordinates of bg circle
        this.mBgCircleCx = width / 2f;
        this.mBgCircleCy = height / 2f;


        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        final Bundle bundle = new Bundle();

        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());

        // Circle
        bundle.putInt(INSTANCE_BG_CIRCLE_COLOR, mBgCircleColor);
        bundle.putInt(INSTANCE_ARC_COLOR, mArcColor);
        bundle.putFloat(INSTANCE_BG_CIRCLE_STROKE_WIDTH, mBgCircleStrokeWidth);

        // Timer information
        bundle.putInt(INSTANCE_TIMER_INFO_TEXT_COLOR, mTimerInfoTextColor);
        bundle.putFloat(INSTANCE_TIMER_INFO_TEXT_SIZE, mTimerInfoTextSize);
        bundle.putFloat(INSTANCE_TIMER_INFO_TEXT_PADDING, mTimerInfoTextPadding);
        bundle.putString(INSTANCE_TIMER_INFO_TEXT, mTimerInfoText);

        // Timer text
        bundle.putInt(INSTANCE_TIMER_TIME_TEXT_COLOR, mTimerTimeTextColor);
        bundle.putFloat(INSTANCE_TIMER_TIME_TEXT_SIZE, mTimerTimeTextSize);
        bundle.putString(INSTANCE_TIMER_TIME_TEXT, mTimerTimeText);

        // TailIcon
        bundle.putInt(INSTANCE_TAIL_ICON_DRAWABLE_ID, mTailIconDrawableId);
        bundle.putBoolean(INSTANCE_TAIL_ICON_VISIBILITY, mTailIconVisibility);
        bundle.putFloat(INSTANCE_TAIL_ICON_PADDING, mTailIconPadding);

        // Circle Button
        bundle.putInt(INSTANCE_CIRCLE_BTN_ICON_DRAWABLE_ID, mCircleBtnIconDrawableId);
        bundle.putBoolean(INSTANCE_CIRCLE_BTN_ICON_VISIBILITY, mCircleBtnVisibility);

        // Alert icon
        bundle.putInt(INSTANCE_ALERT_ICON_DRAWABLE_ID, mAlertIconDrawableId);
        bundle.putBoolean(INSTANCE_ALERT_ICON_VISIBILITY, mAlertIconVisibility);

        // Progress
        bundle.putFloat(INSTANCE_CURRENT_PROGRESS, mCurrentProgress);
        bundle.putFloat(INSTANCE_MAX_PROGRESS, mMaxProgress);
        bundle.putLong(INSTANCE_LEFT_COUNT_DOWN_TIME, mLeftCountDownTime);

        bundle.putBoolean(INSTANCE_IS_TIMER_RUNNING, isTimerRunning);

       /// bundle.putInt(INSTANCE_CURR_STATUS, mCurrStatus);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            final Bundle bundle = (Bundle) state;

            isTimerRunning = bundle.getBoolean(INSTANCE_IS_TIMER_RUNNING);

            // Restore states when timer was running
            if (isTimerRunning()) {

                // Circle
                mBgCircleColor = bundle.getInt(INSTANCE_BG_CIRCLE_COLOR);
                mArcColor = bundle.getInt(INSTANCE_ARC_COLOR);
                mBgCircleStrokeWidth = bundle.getFloat(INSTANCE_BG_CIRCLE_STROKE_WIDTH);

                // Timer information
                mTimerInfoTextColor = bundle.getInt(INSTANCE_TIMER_INFO_TEXT_COLOR);
                mTimerInfoTextSize = bundle.getFloat(INSTANCE_TIMER_INFO_TEXT_SIZE);
                mTimerInfoTextPadding = bundle.getFloat(INSTANCE_TIMER_INFO_TEXT_PADDING);
                mTimerInfoText = bundle.getString(INSTANCE_TIMER_INFO_TEXT);

                // Timer time
                mTimerTimeTextColor = bundle.getInt(INSTANCE_TIMER_TIME_TEXT_COLOR);
                mTimerTimeTextSize = bundle.getFloat(INSTANCE_TIMER_TIME_TEXT_SIZE);
                mTimerTimeText = bundle.getString(INSTANCE_TIMER_TIME_TEXT);

                // Progress
                mCurrentProgress = bundle.getFloat(INSTANCE_CURRENT_PROGRESS);
                mMaxProgress = bundle.getFloat(INSTANCE_MAX_PROGRESS);
                mLeftCountDownTime = bundle.getLong(INSTANCE_LEFT_COUNT_DOWN_TIME);

                // Tail icon
                mTailIconDrawableId = bundle.getInt(INSTANCE_TAIL_ICON_DRAWABLE_ID);
                mTailIconVisibility = bundle.getBoolean(INSTANCE_TAIL_ICON_VISIBILITY);
                mTailIconPadding = bundle.getFloat(INSTANCE_TAIL_ICON_PADDING);

                // Circle button
                mCircleBtnIconDrawableId = bundle.getInt(INSTANCE_CIRCLE_BTN_ICON_DRAWABLE_ID);
                mCircleBtnVisibility = bundle.getBoolean(INSTANCE_CIRCLE_BTN_ICON_VISIBILITY);

                // Alert button
                mAlertIconDrawableId = bundle.getInt(INSTANCE_ALERT_ICON_DRAWABLE_ID);
                mAlertIconVisibility = bundle.getBoolean(INSTANCE_ALERT_ICON_VISIBILITY);

                // Circle
                setBgCircleColor(mBgCircleColor);
                setArcColor(mArcColor);
                setBgCircleStrokeWidth(mBgCircleStrokeWidth);

                // Timer information
                setTimerInfoTextColor(mTimerInfoTextColor);
                setTimerInfoTextSize(mTimerInfoTextSize);
                setTimerInfoTextPadding(mTimerInfoTextPadding);
                setTimerInfoText(mTimerInfoText);

                // Timer time
                setTimerTimeTextColor(mTimerTimeTextColor);
                setTimerTimeTextSize(mTimerTimeTextSize);
                setTimerTimeText(mTimerTimeText);


                // Tail icon
                setTailIconDrawable(mTailIconDrawableId);
                setTailIconVisibility(mTailIconVisibility);
                setTailIconPadding(mTailIconPadding);

                // Circle btn
                setCircleBtnIconDrawable(mCircleBtnIconDrawableId);
                setCircleBtnVisibility(mCircleBtnVisibility);

                // Alert icon
                setAlertIconDrawable(mAlertIconDrawableId);
                setAlertIconVisibility(mAlertIconVisibility);

                // Progress
                setProgress(mCurrentProgress);
                setMaxProgress(mMaxProgress);

                // Stop timer to restart timer
                isTimerRunning = false;
                //updateCurrentStatus();
                //startCountDownTimer(mCurrentProgress, mLeftCountDownTime);
            }
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    /**
     * Set current status of circle timer view
     *
     * @param
     */
    private void updateCurrentStatus(RoomEventModel eventModel) {

        if (eventModel == null) {
            return;
        }

        mAlertIconVisibility = eventModel.isDoNotDisturb();
        mTimerTimeVisibility = !eventModel.isDoNotDisturb();
        mCircleBtnVisibility = !eventModel.isDoNotDisturb() || !eventModel.isOnHold();
        mTailIconVisibility = eventModel.getStatus() == RoomEventModel.BUSY;

        switch (eventModel.getStatus()) {
            case RoomEventModel.AVAILABLE:
                setTimerInfoText("AVAILABLE FOR");
                setTimerInfoTextColor(DEFAULT_TIMER_INFO_TEXT_BLACK_COLOR);
                setBgCircleColor(eventModel.getEventBgColor());
                setArcColor(eventModel.getEventColor());
                setBgCirclePaintStyle(Paint.Style.STROKE);
                break;
            case RoomEventModel.BUSY:
                if (eventModel.isOnHold()) {

                    setTimerInfoText("ON HOLD FOR");
                    setTimerInfoTextColor(DEFAULT_TIMER_INFO_TEXT_BLACK_COLOR);
                    setBgCircleColor(eventModel.getEventBgColor());
                    setArcColor(eventModel.getEventColor());
                    setBgCirclePaintStyle(Paint.Style.STROKE);

                } else if (eventModel.isDoNotDisturb()) {

                    setTimerInfoText("DO NOT DISTURB");
                    setTimerInfoTextColor(DEFAULT_TIMER_INFO_TEXT_WHITE_COLOR);
                    setBgCircleColor(eventModel.getEventBgColor());
                    setArcColor(eventModel.getEventColor());
                    setBgCirclePaintStyle(Paint.Style.FILL_AND_STROKE);

                } else {
                    
                    setTimerInfoText("BUSY UNTIL");
                    setTimerInfoTextColor(DEFAULT_TIMER_INFO_TEXT_BLACK_COLOR);
                    setBgCircleColor(eventModel.getEventBgColor());
                    setArcColor(eventModel.getEventColor());
                    setBgCirclePaintStyle(Paint.Style.STROKE);

                }
                break;
            default:
                break;
        }
    }

    public void startCountDownTimer(RoomEventModel event) {

        if (isTimerRunning()) {
            return;
        }

        if (event.isExpired()) {
            mCountDownListener.onCountDownFinished();
            return;
        }
        mCurrRoomEvent = event;

        float currentProgress = (TimeHelper.getCurrentTimeInMillis() - mCurrRoomEvent.getStartTime()) / (float)mCurrRoomEvent.getDuration();
        updateCurrentStatus(mCurrRoomEvent);

        mAnimator = ValueAnimator.ofFloat(currentProgress * 100, getMaxProgress());
        mAnimator.setDuration(mCurrRoomEvent.getRemainingTime());
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(valueAnimator -> {
            setProgress((float) valueAnimator.getAnimatedValue());
            setLeftCountDownTime(valueAnimator.getDuration() - valueAnimator.getCurrentPlayTime());
            mCountDownListener.onCountDownTicking(valueAnimator.getDuration() - valueAnimator.getCurrentPlayTime());
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isTimerRunning = false;
                setProgress(getMaxProgress());
                setLeftCountDownTime(0);
                mCountDownListener.onCountDownTicking(getLeftCountDownTime());
                mCountDownListener.onCountDownFinished();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        mAnimator.start();

        isTimerRunning = true;
    }

    /**
     * Get left count down time
     *
     * @return
     */
    private long getLeftCountDownTime() {
        return mLeftCountDownTime;
    }

    /**
     * Set left count down time
     *
     * @param milliseconds
     */
    private void setLeftCountDownTime(long milliseconds) {
        this.mLeftCountDownTime = milliseconds;
    }

    /**
     * Running status of count down timer
     *
     * @return
     */
    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    /**
     * Stop count down timer
     */
    public void cancelCountDownTimer() {
        if (mAnimator != null) {
            mAnimator.cancel();
            isTimerRunning = false;
        }
    }

    /**
     * Set max progress
     *
     * @param progress
     */
    public void setMaxProgress(float progress) {
        if (progress > 0) {
            mMaxProgress = progress;
        }
    }

    /**
     * Get max progress
     */
    public float getMaxProgress() {
        return mMaxProgress;
    }


    /**
     * Get current progress
     *
     * @return
     */
    public float getProgress() {
        return mCurrentProgress;
    }

    /**
     * Set current progress
     *
     * @param progress
     */
    private void setProgress(float progress) {
        if (progress >= 0 && progress <= getMaxProgress()) {
            mCurrentProgress = progress;
            rotateDegree = 360f * ((getMaxProgress() - getProgress()) / getMaxProgress());
            invalidate();
        }
    }

    /**
     * Set background circle color
     *
     * @param color
     */
    public void setBgCircleColor(int color) {
        mBgCircleColor = color;
        mBgCirclePaint.setColor(mBgCircleColor);
        invalidate();
    }

    /**
     * Set arc color
     *
     * @param color
     */
    public void setArcColor(int color) {
        mArcColor = color;
        mArcPaint.setColor(mArcColor);
        mOvalPaint.setColor(mArcColor);
        invalidate();
    }

    /**
     * Set timer information text
     *
     * @param info
     */
    public void setTimerInfoText(String info) {
        mTimerInfoText = info;
        invalidate();
    }

    /**
     * Set timer time with text
     *
     * @param time
     */
    public void setTimerTimeText(String time) {
        if (time != null) {
            mTimerTimeText = time;
        }
        invalidate();
    }

    public void setTimerTimeVisibility(boolean visibility) {
        this.mTimerTimeVisibility = visibility;
    }

    /**
     * Set tail icon drawable
     *
     * @param id
     */
    public void setTailIconDrawable(int id) {
        mTailIconDrawableId = id;
        mTailIconDrawable = ContextCompat.getDrawable(getContext(), id);
        invalidate();
    }

    public void setCircleBtnIconDrawable(int id) {
        mCircleBtnIconDrawableId = id;
        mCircleBtnIconDrawable = ContextCompat.getDrawable(getContext(), id);
        invalidate();
    }


    public void setAlertIconDrawable(int id) {
        mAlertIconDrawableId = id;
        mAlertIconDrawable = ContextCompat.getDrawable(getContext(), id);
        invalidate();
    }

    public void setAlertIconVisibility(boolean visibility) {
        this.mAlertIconVisibility = visibility;
    }

    public void setCircleBtnVisibility(boolean visibility) {
        this.mCircleBtnVisibility = visibility;
    }

    /**
     * Set tail icon visibility
     *
     * @param visibility
     */
    public void setTailIconVisibility(boolean visibility) {
        mTailIconVisibility = visibility;
        invalidate();
    }

    /**
     * Set tail icon position padding
     *
     * @param padding
     */
    public void setTailIconPadding(float padding) {
        mTailIconPadding = padding;
        invalidate();
    }

    /**
     * Set timer information padding
     */
    public void setTimerInfoTextPadding(float padding) {
        this.mTimerInfoTextPadding = padding;
        invalidate();
    }

    /**
     * Set circle stroke width
     *
     * @param width
     */
    public void setBgCircleStrokeWidth(float width) {
        this.mBgCircleStrokeWidth = width;
        mBgCirclePaint.setStrokeWidth(width);
        mArcPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * Set timer time text size
     *
     * @param size
     */
    public void setTimerTimeTextSize(float size) {
        this.mTimerTimeTextSize = size;
        mTimerTimeTextPaint.setTextSize(mTimerTimeTextSize);
        invalidate();
    }

    /**
     * Set timer information text size
     *
     * @param size
     */
    public void setTimerInfoTextSize(float size) {
        this.mTimerInfoTextSize = size;
        mTimerInfoTextPaint.setTextSize(mTimerInfoTextSize);
        invalidate();
    }

    /**
     * Set timer information text color
     *
     * @param color
     */
    public void setTimerInfoTextColor(int color) {
        this.mTimerInfoTextColor = color;
        mTimerInfoTextPaint.setColor(mTimerInfoTextColor);
        invalidate();
    }

    /**
     * Set timer time text color
     * @param color
     */
    public void setTimerTimeTextColor(int color) {
        this.mTimerTimeTextColor = color;
        mTimerTimeTextPaint.setColor(mTimerTimeTextColor);
        invalidate();
    }

    private void setBgCirclePaintStyle(Paint.Style style) {
        mBgCirclePaint.setStyle(style);
    }


    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * Set count down listener
     * @param listener
     */
    public void setOnCountDownListener(OnCountDownListener listener) {
        this.mCountDownListener = listener;
    }


    /**
     * Set circle button click listener
     * @param listener
     */
    public void setOnCircleBtnClickListener(OnBtnClickListener listener)  {
        this.mCircleBtnClickListener = listener;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelCountDownTimer();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                RectF circleBtnBounds = new RectF();
                circleBtnBounds.set(mCircleBtnCx - mCircleBtnRadius, mCircleBtnCy - mCircleBtnRadius,
                                    mCircleBtnCx + mCircleBtnRadius, mCircleBtnCy + mCircleBtnRadius);
                if (mCircleBtnVisibility
                    && circleBtnBounds.contains(touchX, touchY)
                    && mCircleBtnClickListener != null) {
                    mCircleBtnClickListener.onClick(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Created by zhangyaozhong on 14/12/2016.
     */

    public interface OnBtnClickListener {
        void onClick(View view);
    }

    /**
     * Created by zhangyaozhong on 08/12/2016.
     */

    public interface OnCountDownListener {
        void onCountDownTicking(long millisUntilFinished);
        void onCountDownFinished();
    }
}
