package com.badoo.meetingroom.domain.entity.intf;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface GoogleAccount extends Entity {
    void setAccountName(String accountName);
    String getAccountName();
}
