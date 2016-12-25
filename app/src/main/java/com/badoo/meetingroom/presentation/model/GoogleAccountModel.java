package com.badoo.meetingroom.presentation.model;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GoogleAccountModel {

    public String accountName;

    public GoogleAccountModel(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
