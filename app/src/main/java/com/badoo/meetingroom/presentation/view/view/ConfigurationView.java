package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public interface ConfigurationView extends LoadDataView {

    void showChooseAccountDialog();

    void showRequestPermissionsDialog();

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode);

    void showAccountNameOnSnackBar(String accountName);

    void showNoGooglePlayServicesOnSnackBar();

    void showRecoverableAuth(UserRecoverableAuthIOException e);

    void showConnectGoogleCalendarSuccessful();

    void setUpRoomListSpinner(List<RoomModel> roomModelList);

    void showRoomStatusView();
}