package com.example.student.gefriertruhapp.History;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import java.util.List;

/**
 * Created by Stefan on 11-10-16.
 */
public class HistoryAdapter extends FragmentPagerAdapter {
    List<TitleFragment> fragments;

    public HistoryAdapter(FragmentManager fm, List<TitleFragment> list) {
        super(fm);
        this.fragments = list;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(fragments.size() < position){
            return "";
        }
        return fragments.get(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        int itemPosition = fragments.indexOf(object);
        if(itemPosition == -1){
            return POSITION_NONE;
        }
        return itemPosition;
    }
}