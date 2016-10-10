package com.example.student.gefriertruhapp.ViewPager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Stefan on 21-05-15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<TitleFragment> _list;

    public ViewPagerAdapter(FragmentManager fm, List<TitleFragment> list) {
        super(fm);
        this._list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return _list.get(position);
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(_list.size() < position){
            return "";
        }
        return _list.get(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        int itemPosition = _list.indexOf(object);
        if(itemPosition == -1){
            return POSITION_NONE;
        }
        return itemPosition;
    }
}
