package com.badoo.meetingroom.presentation.view.component.ballpulseloadingview;

import android.graphics.Canvas;
import android.graphics.Paint;

import android.animation.ValueAnimator;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/10/19.
 */
public class BallPulseSyncIndicator extends Indicator {

    private float[] translateYFloats = new float[3];
    private float radius = 10;

    BallPulseSyncIndicator() {
        translateYFloats[0] = 0;
        translateYFloats[1] = 0;
        translateYFloats[2] = 0;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

        canvas.save();
        canvas.translate(0, translateYFloats[0]);
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.restore();

        canvas.save();
        float translateX = getWidth() / 2f - radius;
        canvas.translate(translateX, translateYFloats[1]);
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.restore();

        canvas.save();
        translateX = getWidth() - 2 * radius;
        canvas.translate(translateX, translateYFloats[2]);
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.restore();
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {

        ArrayList<ValueAnimator> animators = new ArrayList<>();

        int[] delays = new int[]{0, 300, 600};
        for (int i = 0; i < 3; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(0, getHeight() - 2 * radius, 0);
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    translateYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
        }
        return animators;
    }


}