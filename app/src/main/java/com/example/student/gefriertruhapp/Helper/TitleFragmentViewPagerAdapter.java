package com.example.student.gefriertruhapp.Helper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.example.student.gefriertruhapp.Helper.TitleFragment;

import java.util.List;

/**
 * Created by Stefan on 21-05-15.
 */
public class TitleFragmentViewPagerAdapter extends FragmentPagerAdapter {
    private List<TitleFragment> _list;

    public TitleFragmentViewPagerAdapter(FragmentManager fm, List<TitleFragment> list) {
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
