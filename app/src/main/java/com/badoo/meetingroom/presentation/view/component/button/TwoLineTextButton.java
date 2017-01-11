package com.badoo.meetingroom.presentation.view.component.button;

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


    private static final float DEFAULT_TOP_TEXT_SIZE = 72f;
    private static final int DEFAULT_TOP_TEXT_COLOR = Color.BLACK;

    private static final float DEFAULT_BOTTOM_TEXT_SIZE = 32f;
    private static final int DEFAULT_BOTTOM_TEXT_COLOR = Color.BLACK;

    private String mTopText = "5";
    private String mBottomText = "min";

    private float mTopTextSize;
    private float mBottomTextSize;

    private int mTopTextColor;
    private int mBottomTextColor;

    private Paint mTopTextPaint;
    private Paint mBottomTextPaint;


    public TwoLineTextButton(Context context) {
        super(context);

        mTopTextColor = DEFAULT_TOP_TEXT_COLOR;
        mTopTextSize = DEFAULT_TOP_TEXT_SIZE;

        mBottomTextColor = DEFAULT_BOTTOM_TEXT_COLOR;
        mBottomTextSize = DEFAULT_BOTTOM_TEXT_SIZE;

        init();
    }

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
        int diameter = Math.min(getWidth(), getHeight());
        float radius = diameter / 2f;

        if (mTopText != null && mBottomText != null) {
            canvas.drawText(mTopText, radius, radius, mTopTextPaint);
            canvas.drawText(mBottomText, radius, radius + radius / 2f, mBottomTextPaint);
        }
        super.onDraw(canvas);
    }

    public void setTopText(String topText) {
        if (topText == null) {
            return;
        }
        mTopText = topText;
        invalidate();
    }

    public void setBottomText(String bottomText) {
        if (bottomText == null) {
            return;
        }
        mBottomText = bottomText;
        invalidate();
    }
}
