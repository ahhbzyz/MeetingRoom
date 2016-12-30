package com.badoo.meetingroom.presentation.view.component.edittext;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class ExtendedEditText extends EditText {
    public ExtendedEditText(Context context) {
        super(context);
    }

    public ExtendedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.clearFocus();
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
