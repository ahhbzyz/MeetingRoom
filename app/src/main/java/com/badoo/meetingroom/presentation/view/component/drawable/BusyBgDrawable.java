package com.badoo.meetingroom.presentation.view.component.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class BusyBgDrawable extends Drawable {


    private final float CORNER_RADIUS = 20;
    private final int mExpireBgColor = Color.parseColor("#4DD4D4D4");
    private final int mBgColor;

    private float mRemainingProgress;

    private final Paint mBgPaint;
    private final Paint mDividerPaint;
    private final float gap = 40f;

    public BusyBgDrawable(int mBgColor, int dividerColor) {
        this(mBgColor, dividerColor, 1);
    }

    public BusyBgDrawable(int bgColor, int dividerColor, float remainingProgress) {

        mBgColor = bgColor;
        mBgPaint = new Paint();
        mRemainingProgress = remainingProgress;
        mDividerPaint = new Paint();
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(dividerColor);
        mDividerPaint.setStrokeWidth(2);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        float bottomRectHeight = mRemainingProgress * height;
        float topRectHeight = height - bottomRectHeight;

        mBgPaint.setColor(mExpireBgColor);
        canvas.drawRoundRect(new RectF(- CORNER_RADIUS, 0, width, topRectHeight), CORNER_RADIUS, CORNER_RADIUS, mBgPaint);

        mBgPaint.setColor(mBgColor);
        canvas.drawRoundRect(new RectF(-CORNER_RADIUS, topRectHeight, width, height), CORNER_RADIUS, CORNER_RADIUS, mBgPaint);

        float i = 0;
        for (; i < width - bottomRectHeight; i += gap) {
            canvas.drawLine(i, topRectHeight, i + bottomRectHeight, height, mDividerPaint);
        }

        for (float j = i; j < width; j += gap) {
            canvas.drawLine(j, topRectHeight, width, topRectHeight - j + width, mDividerPaint);
        }

        for (float k = bottomRectHeight - gap; k >= 0; k -= gap) {
            canvas.drawLine(k, height, 0, topRectHeight + bottomRectHeight - k, mDividerPaint);
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
