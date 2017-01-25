package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.view.view.EventCreatorDialogView;

/**
 * Created by zhangyaozhong on 25/01/2017.
 */

public interface EventCreatorDialogPresenter extends Presenter{

    void setView(EventCreatorDialogView view);

    void getPerson(String emailAddress);
}
