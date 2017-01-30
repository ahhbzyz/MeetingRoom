package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.view.fragment.RoomListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListFragmentPagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private TreeMap<Integer, List<RoomModel>> mRoomModelListMap;
    private SparseArray<String> mTabTitles;

    @Inject
    public RoomListFragmentPagerAdapter(Context context, FragmentManager fm, TreeMap<Integer, List<RoomModel>> roomModelListMap) {
        super(fm);
        mTabTitles = new SparseArray<>();
        mTabTitles.put(-1, context.getString(R.string.all_rooms));
        mTabTitles.put(0, context.getString(R.string.ground_floor));
        mTabTitles.put(1, context.getString(R.string.first_floor));
        mTabTitles.put(4, context.getString(R.string.fourth_floor));
        mRoomModelListMap = roomModelListMap;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            List<RoomModel> roomModelList = new ArrayList<>();

            for(Map.Entry<Integer,List<RoomModel>> entry : mRoomModelListMap.entrySet()) {
                int key = entry.getKey();
                List<RoomModel> list = mRoomModelListMap.get(key);
                roomModelList.addAll(list);
            }

            return RoomListFragment.newInstance(position, (ArrayList<RoomModel>) roomModelList);

        } else {

            int key = (int) mRoomModelListMap.keySet().toArray()[position - 1];
            return RoomListFragment.newInstance(position, (ArrayList<RoomModel>) mRoomModelListMap.get(key));
        }
    }

    @Override
    public int getCount() {
        return mRoomModelListMap.size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mTabTitles.get(-1);
        }
        int key = (int) mRoomModelListMap.keySet().toArray()[position - 1];
        return mTabTitles.get(key) == null ? "Unknown" : mTabTitles.get(key);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public RoomListFragment getRegisteredFragment(int position) {
        return (RoomListFragment) registeredFragments.get(position);
    }
}
