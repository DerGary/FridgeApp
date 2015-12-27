package com.example.student.gefriertruhapp.ShelfRecyclerView;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.DetailFragments.FridgeDetailFragment;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import java.util.List;


public class FridgeListFragment extends TitleFragment implements ItemClickListener {
    private List<FridgeItem> fridgeItems;
    private View view;
    private String title = "Lager";

    public FridgeListFragment() {
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
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((Dashboard) getActivity()).get_menu().findItem(R.id.add).setVisible(true);
    }

    public void setData(List<FridgeItem> items, String title) {
        fridgeItems = items;
        this.title = title;
        if(view != null){
            setAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.recycler_view);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.view = view;
        setAdapter();
        return view;
    }

    private void setAdapter(){
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter = new FridgeRecycleViewAdapter(fridgeItems, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Object data) {
        FridgeDetailFragment fragment = new FridgeDetailFragment();
        fragment.setData((FridgeItem)data);
        ((Dashboard) getActivity()).changeFragment(fragment, true);
    }

    public String getTitle() {
        return title;
    }
}
