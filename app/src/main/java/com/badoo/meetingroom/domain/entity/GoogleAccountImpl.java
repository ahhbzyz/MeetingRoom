package com.badoo.meetingroom.domain.entity;


/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GoogleAccountImpl implements GoogleAccount {

    private String accountName;

    public GoogleAccountImpl(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String getAccountName() {
        return this.accountName;
    }
}
