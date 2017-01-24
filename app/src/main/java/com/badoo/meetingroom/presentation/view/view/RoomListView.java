package com.badoo.meetingroom.presentation.view.view;

import android.util.SparseArray;

import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by zhangyaozhong on 22/01/2017.
 */

public interface RoomListView extends LoadDataView {
    void setUpViewPager(TreeMap<Integer, List<RoomModel>> roomModelListMap);
}
