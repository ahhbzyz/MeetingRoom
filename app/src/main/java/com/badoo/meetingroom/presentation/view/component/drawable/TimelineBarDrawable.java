package com.badoo.meetingroom.presentation.view.component.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
    private float leftProgress;
    private int orientation = VERTICAL;


    public TimelineBarDrawable(int topColor, int bottomColor, float leftProgress) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.leftProgress = leftProgress;
        this.backgroundPaint = new Paint();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        // get drawable dimensions
        Rect bounds = getBounds();

        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        if (orientation == VERTICAL) {
            float leftBarHeight = leftProgress * height;
            float firstBarHeight = height - leftBarHeight;

            backgroundPaint.setColor(topColor);
            canvas.drawRect(0, 0, width, firstBarHeight, backgroundPaint);

            backgroundPaint.setColor(bottomColor);
            canvas.drawRect(0, firstBarHeight, width, height, backgroundPaint);
        } else {
            float leftBarWidth = leftProgress * width;
            float firstBarWidth = width - leftBarWidth;

            backgroundPaint.setColor(topColor);
            canvas.drawRect(0, 0, firstBarWidth, height, backgroundPaint);

            backgroundPaint.setColor(bottomColor);
            canvas.drawRect(firstBarWidth, 0, width, height, backgroundPaint);
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
}
