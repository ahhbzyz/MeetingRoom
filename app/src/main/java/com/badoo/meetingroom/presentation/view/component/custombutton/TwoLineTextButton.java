package com.badoo.meetingroom.presentation.view.component.custombutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.badoo.meetingroom.R;

/**
 * Created by zhangyaozhong on 15/12/2016.
 */

public class TwoLineTextButton extends ImageButton {


    private static final float DEFAULT_TOP_TEXT_SIZE = 64f;
    private static final int DEFAULT_TOP_TEXT_COLOR = Color.BLACK;

    private static final float DEFAULT_BOTTOM_TEXT_SIZE = 32f;
    private static final int DEFAULT_BOTTOM_TEXT_COLOR = Color.BLACK;



    private float mCircleRadius = 90f;
    private float mCircleStrokeWidth = 2f;

    private String mTopText = "5";
    private String mBottomText = "min";

    private float mTopTextSize;
    private float mBottomTextSize;

    private int mTopTextColor;
    private int mBottomTextColor;

    private Paint mCirclePaint;
    private Paint mTopTextPaint;
    private Paint mBottomTextPaint;


    private float mCircleCx;
    private float mCircleCy;

    public TwoLineTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.TwoLineTextButton,
            0, 0
        );

        try {
            mTopText = ta.getString(R.styleable.TwoLineTextButton_topText);
            mTopTextColor = ta.getColor(R.styleable.TwoLineTextButton_topTextColor, DEFAULT_TOP_TEXT_COLOR);
            mTopTextSize = ta.getFloat(R.styleable.TwoLineTextButton_topTextSize, DEFAULT_TOP_TEXT_SIZE);

            mBottomText = ta.getString(R.styleable.TwoLineTextButton_bottomText);
            mBottomTextColor = ta.getColor(R.styleable.TwoLineTextButton_bottomTextColor, DEFAULT_BOTTOM_TEXT_COLOR);
            mBottomTextSize = ta.getFloat(R.styleable.TwoLineTextButton_bottomTextSize, DEFAULT_BOTTOM_TEXT_SIZE);

        } finally {
            ta.recycle();
        }


        init();
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);

        mTopTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTopTextPaint.setTextSize(mTopTextSize);
        mTopTextPaint.setColor(mTopTextColor);
        mTopTextPaint.setTextAlign(Paint.Align.CENTER);
        Typeface stolzlRegularFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/stolzl_regular.otf");
        mTopTextPaint.setTypeface(stolzlRegularFont);


        mBottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomTextPaint.setColor(mBottomTextColor);
        mBottomTextPaint.setTextSize(mBottomTextSize);
        mBottomTextPaint.setTextAlign(Paint.Align.CENTER);
        mBottomTextPaint.setTypeface(stolzlRegularFont);


        setBackground(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCircleCx, mCircleCy, mCircleRadius, mCirclePaint);

        if (mTopText != null && mBottomText != null) {

            canvas.drawText(mTopText, mCircleCx, mCircleCy, mTopTextPaint);
            canvas.drawText(mBottomText, mCircleCx, mCircleCy + mCircleRadius / 2f, mBottomTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = (int) (2 * (mCircleRadius + mCircleStrokeWidth));
        int desiredHeight = (int) (2 * (mCircleRadius + mCircleStrokeWidth));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        }
        else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        }
        else {
            height = desiredHeight;
        }

        this.mCircleCx = width / 2f;
        this.mCircleCy = height / 2f;

        setMeasuredDimension(width, height);
    }

    public void setTopText(String mTopText) {
        this.mTopText = mTopText;
    }

    public void setBottomText(String mBottomText) {
        this.mBottomText = mBottomText;
    }
}
