package com.badoo.meetingroom.presentation.view.component.customdrawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.badoo.meetingroom.Status;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class BusyBgDrawable extends Drawable {


    private int pastBgColor;
    private int bgColor;

    private float leftProgress;

    private Paint mBgPaint;
    private Paint mDividerPaint;
    private final float gap = 10f;

    public BusyBgDrawable(int bgColor, int dividerColor) {
        this.bgColor = bgColor;
        //pastBgColor = Status.EXPIRED_COLOR_OPACITIY_30;
        mBgPaint = new Paint();

        this.leftProgress = 1f;

        mDividerPaint = new Paint();
        mDividerPaint.setColor(dividerColor);
        mDividerPaint.setStrokeWidth(3);
    }

    public BusyBgDrawable(int pastBgColor, int bgColor, int dividerColor, float leftProgress) {

        this.bgColor = bgColor;
        this.pastBgColor = pastBgColor;

        mBgPaint = new Paint();

        this.leftProgress = leftProgress;

        mDividerPaint = new Paint();
        mDividerPaint.setColor(dividerColor);
        mDividerPaint.setStrokeWidth(3);
    }


    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        float leftRectHeight= leftProgress * height;
        float firstRectHeight = height - leftRectHeight;

        mBgPaint.setColor(pastBgColor);
        canvas.drawRect(0, 0, width, firstRectHeight, mBgPaint);

        mBgPaint.setColor(bgColor);
        canvas.drawRect(0, firstRectHeight, width, height, mBgPaint);

        for (float i = 0; i <= width; i += gap) {
            canvas.drawLine(i, firstRectHeight, leftRectHeight + i, height, mDividerPaint);
        }

        for (float i = firstRectHeight; i <= height; i += gap) {
            canvas.drawLine(0, i, leftRectHeight - (i - firstRectHeight), height, mDividerPaint);
        }
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
