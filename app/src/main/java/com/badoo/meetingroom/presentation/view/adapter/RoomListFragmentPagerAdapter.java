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

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListFragmentPagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private SparseArray<List<RoomModel>> mRoomModelListMap;
    private SparseArray<String> mTabTitles;
    private Context mContext;

    @Inject
    public RoomListFragmentPagerAdapter(Context context, FragmentManager fm, SparseArray<List<RoomModel>> roomModelListMap) {
        super(fm);
        mContext = context;
        mTabTitles = new SparseArray<>();
        mTabTitles.put(0, mContext.getString(R.string.all_rooms));
        mTabTitles.put(1, mContext.getString(R.string.first_floor));
        mTabTitles.put(4, mContext.getString(R.string.fourth_floor));

        mRoomModelListMap = roomModelListMap;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            List<RoomModel> roomModelList = new ArrayList<>();
            for (int i = 0; i < mRoomModelListMap.size(); i++) {
                int key = mRoomModelListMap.keyAt(i);
                List<RoomModel> list = mRoomModelListMap.get(key);
                roomModelList.addAll(list);
            }
            return RoomListFragment.newInstance(position, (ArrayList<RoomModel>) roomModelList);
        } else {
            return RoomListFragment.newInstance(position, (ArrayList<RoomModel>) mRoomModelListMap.get(position));
        }
    }

    @Override
    public int getCount() {
        return mRoomModelListMap.size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position) == null ? "Unknown" :mTabTitles.get(position);
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
