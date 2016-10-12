package com.example.student.gefriertruhapp.StoreList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Helper.RecyclerListFragment;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.FridgeList.ItemClickListener;

import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */

public class StoresListFragment extends RecyclerListFragment implements ItemClickListener {
    private String title = "Lager verwalten";
    public StoresListFragment(){}
    private List<Store> stores;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void setAdapter(){
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter = new StoreRecycleViewAdapter(stores, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_settings, menu);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getTitle());
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_settings_add){
            StoreDetailFragment fragment = new StoreDetailFragment();
            ((Dashboard) getActivity()).changeFragment(fragment, true);
            return true;
        } else if(id == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setData(DataBaseSingleton.getInstance().getStores());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setData(List<Store> items) {
        stores = items;
        if(view != null){
            setAdapter();
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onItemClick(Object data) {
        StoreDetailFragment fragment = new StoreDetailFragment();
        fragment.setData((Store)data);
        ((Dashboard) getActivity()).changeFragment(fragment, true);
    }

}
