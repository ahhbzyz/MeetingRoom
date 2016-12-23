package com.badoo.meetingroom.presentation.view;

import android.content.Context;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface LoadDataView {

    void showLoadingData();

    void hideLoadingData();

    void showRetryLoading();

    void hideRetryLoading();

    void showError(String message);

    Context context();
}
