package com.badoo.meetingroom.presentation.view.view;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface GetCredentialView extends LoadDataView {
    void showChooseAccountDialog();
    void showRequestPermissionsDialog();
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode);
    void showAccountNameOnSnackBar(String accountName);
    void showNoGooglePlayServicesOnSnackBar();
    void showRecoverableAuth(UserRecoverableAuthIOException e);
    void showConnectGoogleCalendarSuccessful();
}
