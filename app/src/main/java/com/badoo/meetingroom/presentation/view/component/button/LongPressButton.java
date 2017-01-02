package com.badoo.meetingroom.presentation.view.component.button;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageButton;


/**
 * Created by zhangyaozhong on 14/12/2016.
 */

public class LongPressButton extends ImageButton {


    private float mCircleCx;
    private float mCircleCy;

    private float mCircleRadius = 90f;
    private Paint mCirclePaint;


    private Paint mGapCirclePaint;

    private float mCountDownCircleWidth = 6f;

    private Paint mCountDownCirclePaint;

    /**
     * Circle degree
     */
    private float startDegree = 270f;
    private float rotateDegree = 360f;
    private Paint mArcPaint;
    private RectF mArcOval;

    private ValueAnimator mAnimator;

    private OnCountDownListener mCountDownListener;

    public LongPressButton(Context context) {
        super(context);
        init();
    }

    public LongPressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.parseColor("#F5584F"));

        mGapCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGapCirclePaint.setStyle(Paint.Style.STROKE);
        mGapCirclePaint.setStrokeWidth(mCountDownCircleWidth);
        mGapCirclePaint.setColor(Color.TRANSPARENT);


        mCountDownCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCountDownCirclePaint.setStyle(Paint.Style.STROKE);
        mCountDownCirclePaint.setStrokeWidth(mCountDownCircleWidth);
        mCountDownCirclePaint.setColor(Color.parseColor("#FFE8E8"));

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mCountDownCircleWidth);
        mArcPaint.setColor(Color.parseColor("#F5584F"));
        mArcOval = new RectF();

        this.setBackground(null);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int diameter = Math.min(getWidth(), getHeight());
        mCircleCx = diameter / 2f;
        mCircleCy = diameter / 2f;
        mCircleRadius = diameter / 2f - 2 * mCountDownCircleWidth;

        canvas.drawCircle(mCircleCx, mCircleCy, mCircleRadius, mCirclePaint);

        float gapCircleRadius = mCircleRadius + mCountDownCircleWidth / 2f;
        canvas.drawCircle(mCircleCx, mCircleCy, gapCircleRadius, mGapCirclePaint);

        float countDownCircleRadius = gapCircleRadius + mCountDownCircleWidth;
        canvas.drawCircle(mCircleCx, mCircleCy, countDownCircleRadius, mCountDownCirclePaint);

        mArcOval.set(mCircleCx - countDownCircleRadius, mCircleCy - countDownCircleRadius, mCircleCx + countDownCircleRadius, mCircleCy + countDownCircleRadius);
        canvas.drawArc(mArcOval, startDegree, rotateDegree, false, mArcPaint);

        super.onDraw(canvas);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (pressed) {
            mAnimator = ValueAnimator.ofFloat(360f, 0f);
            mAnimator.setDuration(1000);
            mAnimator.addUpdateListener(valueAnimator -> updateRotateDegree((Float) valueAnimator.getAnimatedValue()));

            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (rotateDegree == 0) {
                        if (mCountDownListener != null) {
                            mCountDownListener.onCountDownFinished();
                        }
                    }

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            mAnimator.start();
        } else {
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            updateRotateDegree(360);
        }

    }

    private void updateRotateDegree(float degree) {
        this.rotateDegree = degree;
        invalidate();
    }

    public void setOnCountDownListener(OnCountDownListener listener) {
        this.mCountDownListener = listener;
    }
}
