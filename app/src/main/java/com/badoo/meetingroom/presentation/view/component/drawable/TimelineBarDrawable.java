package com.badoo.meetingroom.presentation.view.component.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class TimelineBarDrawable extends Drawable {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private final Paint backgroundPaint;
    private int topColor;
    private int bottomColor;
    private float remainingProgress = 1f;
    private int orientation = VERTICAL;

    private int leftBarLeftRadius = 0;
    private int leftBarRightRadius = 0;
    private int rightBarLeftRadius = 0;
    private int rightBarRightRadius = 0;

    private float dashWidth;

    private final int CORNER_RADIUS = 10;

    private boolean hasDashes;
    public static final int numOfDashes = 3;


    public TimelineBarDrawable(int topColor, int bottomColor) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.backgroundPaint = new Paint();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // get drawable dimensions
        Rect bounds = getBounds();

        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        if (orientation == VERTICAL) {
            float bottomBarHeight = remainingProgress * height;
            float topBarHeight = height - bottomBarHeight;

            backgroundPaint.setColor(topColor);
            canvas.drawRect(0, 0, width, topBarHeight, backgroundPaint);

            backgroundPaint.setColor(bottomColor);
            canvas.drawRect(0, topBarHeight, width, height, backgroundPaint);

        } else {

            float rightBarWidth = remainingProgress * width;
            float leftBarWidth = width - rightBarWidth;

            float leftBarLeft = leftBarLeftRadius == 0 ? 0 : leftBarWidth / 2f;
            float leftBarRight = leftBarRightRadius == 0 ? 0 : leftBarWidth / 2f;


            // Draw right bar first to have dashes covered by left bar
            if (!hasDashes) {

                float rightBarLeft = rightBarLeftRadius == 0 ? 0 : rightBarWidth / 2f;
                float rightBarRight = rightBarRightRadius == 0 ? 0 : rightBarWidth / 2f;

                backgroundPaint.setColor(bottomColor);
                canvas.drawRect(leftBarWidth + rightBarLeft, 0, width - rightBarRight, height, backgroundPaint);
                canvas.drawRoundRect(new RectF(leftBarWidth, 0, width, height), CORNER_RADIUS, CORNER_RADIUS, backgroundPaint);

            } else {

                backgroundPaint.setColor(bottomColor);
                canvas.drawRect(leftBarWidth, 0, width - numOfDashes * 2 * dashWidth, height, backgroundPaint);

                for(int i = 0; i < numOfDashes * 2; i ++) {
                    int color = i % 2 == 0 ? Color.TRANSPARENT : bottomColor;
                    backgroundPaint.setColor(color);
                    canvas.drawRect(width - numOfDashes * 2 * dashWidth + i * dashWidth, 0, width - numOfDashes * 2 * dashWidth + (i + 1) * dashWidth, height, backgroundPaint);
                }

            }

            // draw left bar
            backgroundPaint.setColor(topColor);
            canvas.drawRect(0 + leftBarLeft, 0, leftBarWidth - leftBarRight, height, backgroundPaint);
            canvas.drawRoundRect(new RectF(0, 0, leftBarWidth, height), CORNER_RADIUS, CORNER_RADIUS, backgroundPaint);
        }
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        invalidateSelf();
    }

    public void setRemainingProgress(float remainingProgress) {
        this.remainingProgress = remainingProgress;
        invalidateSelf();
    }

    public void setHasDashes(boolean hasDashes, float dashWidth) {
        this.hasDashes = hasDashes;
        this.dashWidth = dashWidth;
    }

    public void setCornerRadius(int leftBarLeftRadius, int leftBarRightRadius, int rightBarLeftRadius, int rightBarRightRadius) {
        this.leftBarLeftRadius = leftBarLeftRadius;
        this.leftBarRightRadius = leftBarRightRadius;
        this.rightBarLeftRadius = rightBarLeftRadius;
        this.rightBarRightRadius = rightBarRightRadius;
        invalidateSelf();
    }
}
