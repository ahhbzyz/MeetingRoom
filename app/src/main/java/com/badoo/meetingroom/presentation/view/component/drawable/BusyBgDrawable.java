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
    private List<Line> mLines;

    public BusyBgDrawable(int mBgColor, int dividerColor) {
        this(mBgColor, dividerColor, 1);
    }

    public BusyBgDrawable(int bgColor, int dividerColor, float remainingProgress) {

        mBgColor = bgColor;
        mBgPaint = new Paint();
        mRemainingProgress = remainingProgress;
        mDividerPaint = new Paint();
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(Color.WHITE);
        mDividerPaint.setStrokeWidth(2);

        mLines = new ArrayList<>();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
//
//        float width = bounds.right - bounds.left;
//        float height = bounds.bottom - bounds.top;
//
//        float bottomRectHeight = mRemainingProgress * height;
//        float topRectHeight = height - bottomRectHeight;
//
//        for (float i = - bottomRectHeight; i < width - CORNER_RADIUS; i += gap) {
//            mLines.add(new Line(i, topRectHeight, i + bottomRectHeight, height));
//        }
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


//        for(Line l : mLines) {
//            canvas.drawLine(l.startX, l.startY, l.stopX, l.stopY, mDividerPaint);
//        }

//        Path path = new Path();
//
//        //canvas.drawLine(0, 0, width, height, mDividerPaint);
//
        float i = 0;
        for (; i < width - bottomRectHeight; i += gap) {
            canvas.drawLine(i, topRectHeight, i + bottomRectHeight, height, mDividerPaint);
        }

        for (float j = i; j < width; j += gap) {
            canvas.drawLine(j, topRectHeight, width, topRectHeight - j + width, mDividerPaint);
        }

        for (float k = bottomRectHeight - gap; k >= 0; k -= gap) {
            canvas.drawLine(k, height, 0, bottomRectHeight - k, mDividerPaint);
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


    class Line {

        float startX, startY, stopX, stopY;

        Line(float startX, float startY, float stopX, float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }
    }
}
