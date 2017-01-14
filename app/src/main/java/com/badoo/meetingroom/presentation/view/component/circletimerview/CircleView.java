package com.badoo.meetingroom.presentation.view.component.circletimerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;


/**
 * Created by zhangyaozhong on 06/12/2016.
 */

public class CircleView extends View {

    /**
     * Circle timer progress
     */
    private float mMaxProgress = 100.0f;
    private float mRotateDegree = 360f;

    /**
     * Default attributes
     */
    // Circle attributes
    final float DEFAULT_CIRCLE_RADIUS = dp2px(110);
    final float DEFAULT_CIRCLE_STROKE_WIDTH = dp2px(40);

    final int DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.parseColor("#69E27E");
    final int DEFAULT_CIRCLE_COLOR = Color.parseColor("#69E27E");

    /**
     * Circle attributes
     */
    private float mCircleCx;
    private float mCircleCy;
    private float mCircleRadius;
    private float mCircleStrokeWidth;

    /**
     * Circle degree
     */
    float CIRCLE_START_DEGREE = 270f;


    /**
     * Circle Paint
     */
    private Paint mCircleBackgroundPaint;
    private RectF mCircleRect;
    private Paint mCirclePaint;

    private Paint mTailCirclePaint;


    /**
     * Circle Color
     */
    private int mCircleBackgroundColor;
    private int mCircleColor;


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
    private Drawable mAlertIconDrawable;
    private boolean mAlertIconVisibility;


    /**
     * Count down listener
     */
    private OnCountDownListener mCountDownListener;

    private ValueAnimator mRotateAnimator;

    private Context mContext;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleView,
                0, 0
        );

        try {
            mCircleRadius = ta.getFloat(R.styleable.CircleView_bgCircleRadius, DEFAULT_CIRCLE_RADIUS);
            mCircleStrokeWidth = ta.getFloat(R.styleable.CircleView_bgCircleStrokeWidth, DEFAULT_CIRCLE_STROKE_WIDTH);
            mCircleBackgroundColor = ta.getColor(R.styleable.CircleView_bgCircleColor, DEFAULT_CIRCLE_BACKGROUND_COLOR);
            mCircleColor = ta.getColor(R.styleable.CircleView_bgArcColor, DEFAULT_CIRCLE_COLOR);
        } finally {
            ta.recycle();
        }

        init();
    }

    /**
     * Initial paints
     */
    private void init() {
        // Circle background paint
        mCircleBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleBackgroundPaint.setStyle(Paint.Style.STROKE);
        mCircleBackgroundPaint.setStrokeWidth(mCircleStrokeWidth);
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);

        // Circle paint
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mCirclePaint.setColor(mCircleColor);

        // Tail circle paint
        mTailCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTailCirclePaint.setColor(mCircleColor);
        mCircleRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw circle background
        canvas.drawCircle(mCircleCx, mCircleCy, mCircleRadius, mCircleBackgroundPaint);

        // draw static tail circle
        canvas.drawCircle(mCircleCx, mCircleCy - (mCircleRadius), mCircleStrokeWidth / 2f, mTailCirclePaint);

        // draw dynamic tail circle
        float ovalX = mCircleCx + mCircleRadius * (float) Math.cos((mRotateDegree - 90f) * Math.PI / 180f);
        float ovalY = mCircleCy + mCircleRadius * (float) Math.sin((mRotateDegree - 90f) * Math.PI / 180f);
        canvas.drawCircle(ovalX, ovalY, mCircleStrokeWidth / 2f, mTailCirclePaint);

        // draw circle
        mCircleRect.set(mCircleCx - mCircleRadius, mCircleCy - mCircleRadius, mCircleCx + mCircleRadius, mCircleCy + mCircleRadius);
        canvas.drawArc(mCircleRect, CIRCLE_START_DEGREE, mRotateDegree, false, mCirclePaint);

        // draw tail icon
        if (mTailIconVisibility && mTailIconDrawable != null) {
            float iconX = mCircleCx + mCircleRadius * (float) Math.cos((mRotateDegree - 90f - mTailIconPadding) * Math.PI / 180f);
            float iconY = mCircleCy + mCircleRadius * (float) Math.sin((mRotateDegree - 90f - mTailIconPadding) * Math.PI / 180f);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            // Rotate tail icon based on current rotated degree
            canvas.rotate(mRotateDegree - 360f, iconX, iconY);
            mTailIconDrawable.setBounds((int) (iconX - mCircleStrokeWidth / 4f), (int) (iconY - mCircleStrokeWidth / 4f),
                    (int) (iconX + mCircleStrokeWidth / 4f), (int) (iconY + mCircleStrokeWidth / 4f));
            mTailIconDrawable.draw(canvas);
            canvas.restore();
        }

        // Draw alert icon
        if (mAlertIconVisibility && mAlertIconDrawable != null) {
            float originalWidth = mAlertIconDrawable.getIntrinsicWidth();
            float originalHeight = mAlertIconDrawable.getIntrinsicHeight();

            float ratio = originalWidth / originalHeight;

            float iconWidth = mCircleRadius / 2f;
            float iconHeight = iconWidth / ratio;

            mAlertIconDrawable.setBounds((int) (mCircleCx - iconWidth / 2f), (int) (mCircleCy - iconHeight),
                (int) (mCircleCx + iconWidth / 2f), (int) (mCircleCy));
            mAlertIconDrawable.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 2 * (int) mCircleRadius + (int) mCircleStrokeWidth;
        int desiredHeight = 2 * (int) mCircleRadius + (int) mCircleStrokeWidth;

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
        mCircleRadius = Math.min(width, height) / 2f - mCircleStrokeWidth / 2f;

        // Set coordinates of bg circle
        mCircleCx = width / 2f;
        mCircleCy = height / 2f;

        setMeasuredDimension(width, height);
    }

    /**
     * Set current status of circle timer view
     *
     * @param
     */

    public void startCountDownTimer(long startTime, long endTime) {

        if (mRotateAnimator != null && mRotateAnimator.isRunning()) {
            mRotateAnimator.removeAllListeners();
            mRotateAnimator.cancel();
        }

        if (endTime < startTime || endTime < TimeHelper.getCurrentTimeInMillis()) {
            return;
        }

        float currentProgress = ((TimeHelper.getCurrentTimeInMillis() - startTime) / (float)(endTime - startTime));

        mRotateDegree = 360f * ((getMaxProgress() - currentProgress * 100) / getMaxProgress());

        mRotateAnimator = ValueAnimator.ofFloat(mRotateDegree, 0f);

        long remainingTime = endTime - TimeHelper.getCurrentTimeInMillis();

        mRotateAnimator.setDuration(remainingTime);

        mRotateAnimator.setInterpolator(new LinearInterpolator());

        mRotateAnimator.addUpdateListener(valueAnimator -> {
            setRotateDegree((Float) valueAnimator.getAnimatedValue());
            mCountDownListener.onCountDownTicking(valueAnimator.getDuration() - valueAnimator.getCurrentPlayTime() + 1000);
        });

        mRotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCountDownListener.onCountDownTicking(0);
                mCountDownListener.onCountDownFinished();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        mRotateAnimator.start();
    }

    public void setRotateDegree(float degree) {
        mRotateDegree = degree;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void stopCountDown() {
        if (mRotateAnimator != null && mRotateAnimator.isRunning()) {
            mRotateAnimator.removeAllListeners();
            mRotateAnimator.cancel();
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

    public void setColorTheme(int circleColor, int circleBackgroundColor) {
        mCircleColor = circleColor;
        mTailCirclePaint.setColor(mCircleColor);
        mCirclePaint.setColor(mCircleColor);
        mCircleBackgroundColor = circleBackgroundColor;
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Set background circle color
     *
     * @param color
     */
    public void setCircleBackgroundColor(int color) {
        mCircleBackgroundColor = color;
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);
        invalidate();
    }

    /**
     * Set arc color
     *
     * @param color
     */
    public void setCircleColor(int color) {
        mCircleColor = color;
        mTailCirclePaint.setColor(mCircleColor);
        mCirclePaint.setColor(mCircleColor);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Set tail icon drawable
     *
     * @param id
     */
    public void setTailIconDrawable(int id) {
        mTailIconDrawable = ContextCompat.getDrawable(getContext(), id);
        ViewCompat.postInvalidateOnAnimation(this);
    }


    public void setAlertIconDrawable(int id) {
        mAlertIconDrawable = ContextCompat.getDrawable(getContext(), id);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setAlertIconVisibility(boolean visibility) {
        this.mAlertIconVisibility = visibility;
    }

    /**
     * Set tail icon visibility
     *
     * @param visibility
     */
    public void setTailIconVisibility(boolean visibility) {
        mTailIconVisibility = visibility;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Set tail icon position padding
     *
     * @param padding
     */
    public void setTailIconPadding(float padding) {
        mTailIconPadding = padding;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Set circle stroke width
     *
     * @param width
     */
    public void setCircleStrokeWidth(float width) {
        this.mCircleStrokeWidth = width;
        mCircleBackgroundPaint.setStrokeWidth(width);
        mCirclePaint.setStrokeWidth(width);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }


    /**
     * Set count down listener
     * @param listener
     */
    public void setOnCountDownListener(OnCountDownListener listener) {
        this.mCountDownListener = listener;
    }

    public void setCircleBackgroundPaintStyle(Paint.Style style) {
        this.mCircleBackgroundPaint.setStyle(style);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Created by zhangyaozhong on 08/12/2016.
     */
    public interface OnCountDownListener {
        void onCountDownTicking(long millisUntilFinished);
        void onCountDownFinished();
    }
}