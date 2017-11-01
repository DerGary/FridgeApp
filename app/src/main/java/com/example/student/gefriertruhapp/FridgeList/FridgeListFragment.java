package com.example.student.gefriertruhapp.FridgeList;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.Helper.TitleFragment;

import java.util.List;


public class FridgeListFragment extends TitleFragment implements ItemClickListener {
    private List<FridgeItem> fridgeItems;
    private View view;
    private String title = "Lager";
    private Store store;
    private FridgeRecycleViewAdapter adapter;
    private RecyclerView listView;
    private ItemMarkedListener markedListener;

    public FridgeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setData(List<FridgeItem> items, Store store, ItemMarkedListener markedListener) {
        fridgeItems = items;
        this.title = store.getName();
        this.store = store;
        this.markedListener = markedListener;
        if(adapter != null){
            setDataForAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
            listView = (RecyclerView) view.findViewById(R.id.recycler_view);
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            adapter = new FridgeRecycleViewAdapter(new FridgeViewHolderBuilder(), fridgeItems, this, markedListener);
            listView.setAdapter(adapter);
            this.view = view;
        } else {
            setDataForAdapter();
        }
        return view;
    }

    private void setDataForAdapter(){
        adapter.setData(fridgeItems, this, markedListener);
        adapter.notifyDataSetChanged();
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

    public Store getStore() {
        return store;
    }
}
