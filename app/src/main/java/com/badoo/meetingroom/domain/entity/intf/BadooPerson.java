package com.badoo.meetingroom.domain.entity.intf;

/**
 * Created by zhangyaozhong on 13/01/2017.
 */

public interface BadooPerson {
    String getFamilyName();

    void setFamilyName(String familyName);

    String getGivenName();

    void setGivenName(String givenName);

    String getMiddleName();

    void setMiddleName(String middleName);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getEmailAddress();

    void setEmailAddress(String email);

    String getAvatarUrl();

    void setAvatarUrl(String avatar);
}
