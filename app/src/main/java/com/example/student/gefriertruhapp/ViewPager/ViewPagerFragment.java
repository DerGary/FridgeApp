package com.example.student.gefriertruhapp.ViewPager;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 21-05-15.
 */
public class ViewPagerFragment extends Fragment {
    private ViewPager _pager;
    private ViewPagerAdapter _pagerAdapter;
    private View _view;
    private List<TitleFragment> _list = new ArrayList<TitleFragment>();
    private Context _context;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    public void setData(Context context, List<ShelfItem> shelfItems, List<FridgeItem> fridgeItems) {
        this._context = context;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_view != null) {
            return _view;
        }
        _view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        _pager = (ViewPager) _view.findViewById(R.id.pager);
        _pagerAdapter = new ViewPagerAdapter(getFragmentManager(), _list);
        _pager.setAdapter(_pagerAdapter);
        return _view;
    }

}
