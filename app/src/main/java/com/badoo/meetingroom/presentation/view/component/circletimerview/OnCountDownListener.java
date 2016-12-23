package com.badoo.meetingroom.presentation.view.component.circletimerview;

/**
 * Created by zhangyaozhong on 08/12/2016.
 */

public interface OnCountDownListener {
    void onCountDownTicking(long millisUntilFinished);
    void onCountDownFinished();
}
