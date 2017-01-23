package com.badoo.meetingroom.presentation.view.view;

import android.util.SparseArray;

import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;

/**
 * Created by zhangyaozhong on 22/01/2017.
 */

public interface RoomsView extends LoadDataView {
    void setUpViewPager(SparseArray<List<RoomModel>> roomModelListMap);
}
