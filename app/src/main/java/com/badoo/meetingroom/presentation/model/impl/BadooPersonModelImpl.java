package com.badoo.meetingroom.presentation.model.impl;

import com.badoo.meetingroom.presentation.model.intf.BadooPersonModel;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */
public class BadooPersonModelImpl implements BadooPersonModel {

    private String familyName;
    private String givenName;
    private String middleName;
    private String displayName;
    private String email;
    private String avatarUrl;

    @Override
    public String getFamilyName() {
        return familyName;
    }
    @Override
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    @Override
    public String getGivenName() {
        return givenName;
    }
    @Override
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    @Override
    public String getMiddleName() {
        return middleName;
    }
    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getEmailAddress() {
        return email;
    }

    @Override
    public void setEmailAddress(String email) {
        this.email = email;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
