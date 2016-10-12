package com.example.student.gefriertruhapp.History;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Serialization.FileAccess;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Helper.TitleFragment;
import com.example.student.gefriertruhapp.Helper.TitleFragmentViewPagerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Stefan on 11-10-16.
 */


public class HistoryViewPagerFragment extends Fragment {
    private ViewPager pager;
    private TitleFragmentViewPagerAdapter pagerAdapter;
    private View view;
    private List<HistoryFragment> fragmentList = new ArrayList<>();
    private Queue<HistoryFragment> fragmentQueue = new LinkedList<>();


    public HistoryViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Verlauf");
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    public void setData() {
        Map<String, String> history = FileAccess.readHistory(7);


        while(fragmentList.size() < history.size()){
            if(fragmentQueue.size() > 0){
                fragmentList.add(fragmentQueue.poll());
            } else {
                fragmentList.add(new HistoryFragment());
            }
        }
        while(fragmentList.size() > history.size()){
            HistoryFragment fragment = fragmentList.remove(history.size());
            fragmentQueue.add(fragment);
        }
        int i = 0;
        for(Map.Entry<String,String> entry : history.entrySet()){
            fragmentList.get(i).setData(entry.getKey(), entry.getValue());
            i++;
        }

        if(pagerAdapter != null) {
            try {
                pagerAdapter.notifyDataSetChanged();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        }
        if(pager == null) {
            pager = (ViewPager) view.findViewById(R.id.pager);
        }
        setData();

        if(pagerAdapter == null) {
            pagerAdapter = new TitleFragmentViewPagerAdapter(getChildFragmentManager(), (List<TitleFragment>) (List<?>) fragmentList);
            pager.setAdapter(pagerAdapter);
        }
        return view;
    }

    public int currentView(){
        return pager.getCurrentItem();
    }
}
