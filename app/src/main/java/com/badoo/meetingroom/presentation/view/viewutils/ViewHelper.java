package com.badoo.meetingroom.presentation.view.viewutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;

/**
 * Created by yaozhong on 10/12/2016.
 */

public class ViewHelper {

    public static Drawable createScaleDrawable(Context context, int resId, int width, int height) {
        Drawable origin = ContextCompat.getDrawable(context, resId);
        Bitmap bitmap = ((BitmapDrawable) origin).getBitmap();
        Drawable processed = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
        //if (bitmap != null)
            //bitmap.recycle();
        return processed;
    }

    public static LinearLayout addTextUnderBtn(Context context, View btnView, String text) {
        LinearLayout linearLayout = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(btnView);


        LayoutParams tvParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        tvParams.setMargins(0, 16, 0, 0);
        TextView tv = new TextView(context);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(tvParams);
        linearLayout.addView(tv);
        return linearLayout;
    }

    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
