package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.view.view.RoomListFragmentView;

import java.util.List;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public interface RoomListPresenter extends Presenter {

    void setView(RoomListFragmentView view);

    void getRoomEvents(List<RoomModel> roomModelList);

    void setPage(int page);
}
