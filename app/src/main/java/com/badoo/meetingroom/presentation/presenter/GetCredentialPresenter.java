package com.badoo.meetingroom.presentation.presenter;

import android.support.annotation.NonNull;

import com.badoo.meetingroom.presentation.view.GetCredentialView;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface GetCredentialPresenter extends Presenter {

    void setView(@NonNull GetCredentialView getCredentialView);
}
