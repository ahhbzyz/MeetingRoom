package com.badoo.meetingroom.presentation.view;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface GetCredentialView {
    boolean isGooglePlayServicesAvailable();
    int getGooglePlayServicesStatusCode();
    boolean checkGooglePlayServicesAvailability(final int connectionStatusCode);
    void showNoGooglePlayServicesToast();

    boolean hasPermissionToAccessContacts();

    String getAccountNameFromLocal();
    void storeGoogleAccountName(String accountName);


    void showChooseAccountDialog();
    void showRequestPermissionsDialog();
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode);
}
