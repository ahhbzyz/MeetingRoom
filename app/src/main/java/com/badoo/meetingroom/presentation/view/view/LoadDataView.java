package com.badoo.meetingroom.presentation.view.view;

import android.content.Context;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface LoadDataView {

    void showLoadingData(String message);

    void dismissLoadingData();

    void showError(String message);

    Context context();
}
