package com.badoo.meetingroom.presentation.model;

import android.support.annotation.NonNull;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GoogleAccountModel {

    public String accountName;

    public GoogleAccountModel(@NonNull String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
