package com.badoo.meetingroom.presentation.view.component.custombutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;


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
        super.onDraw(canvas);

        canvas.drawCircle(mCircleCx, mCircleCy, mCircleRadius, mCirclePaint);

        float gapCircleRadius = mCircleRadius + mCountDownCircleWidth / 2f;
        canvas.drawCircle(mCircleCx, mCircleCy, gapCircleRadius, mGapCirclePaint);

        float countDownCircleRadius = gapCircleRadius + mCountDownCircleWidth;
        canvas.drawCircle(mCircleCx, mCircleCy, countDownCircleRadius, mCountDownCirclePaint);

        mArcOval.set(mCircleCx - countDownCircleRadius, mCircleCy - countDownCircleRadius, mCircleCx + countDownCircleRadius, mCircleCy + countDownCircleRadius);
        canvas.drawArc(mArcOval, startDegree, rotateDegree, false, mArcPaint);

        if (getDrawable() != null) {
            getDrawable().setBounds((int) (mCircleCx - mCircleRadius / 2f), (int) (mCircleCy - mCircleRadius / 2f), (int) (mCircleCx + mCircleRadius / 2f), (int) (mCircleCy + mCircleRadius / 2f));
            getDrawable().draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int desiredWidth = (int)(2 * (mCircleRadius + 2 * mCountDownCircleWidth));
        int desiredHeight = (int)(2 *  (mCircleRadius + 2 * mCountDownCircleWidth));

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

        this.mCircleCx = width / 2f;
        this.mCircleCy = height / 2f;

        setMeasuredDimension(width, height);
    }


    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (pressed) {
            mAnimator = ValueAnimator.ofFloat(360f, 0f);
            mAnimator.setDuration(1000);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    updateRotateDegree((Float) valueAnimator.getAnimatedValue());
                }
            });

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
