package com.badoo.meetingroom.presentation.view.view;

import android.content.Context;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface LoadDataView {

    void showLoadingData(boolean visibility);

    void showRetryLoading(boolean visibility);

    void showError(String message);

    Context context();
}