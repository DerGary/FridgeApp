package com.example.student.gefriertruhapp.ViewPager;

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

import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Settings.Store;
import com.example.student.gefriertruhapp.ShelfRecyclerView.FridgeListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Stefan on 21-05-15.
 */
public class ViewPagerFragment extends Fragment {
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    private View view;
    private List<FridgeListFragment> fragmentList = new ArrayList<FridgeListFragment>();
    private String query;
    private Queue<FridgeListFragment> fragmentQueue = new LinkedList<>();


    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setSearchQuery(String query){
        if(query != null) {
            this.query = query.toLowerCase();
        }else{
            this.query = null;
        }
        setData();
        setActionBar();
    }

    public boolean onBackPressed(){
        if(query == null){
            return false;
        }
        setSearchQuery(null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            setSearchQuery(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBar();
    }

    public void setActionBar(){
        if(query != null && query.length() > 0) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("\"" + query + "\"");
        }else{
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Gefriertruhen App");
        }
    }

    @SuppressWarnings("unchecked")
    public void setData() {
        ArrayList<Store> stores = new ArrayList<>(DataBaseSingleton.getInstance().getStores());
        Store buyStore = new Store("Einkaufsliste", "");

        while(fragmentList.size() < stores.size() + 1){
            if(fragmentQueue.size() > 0){
                fragmentList.add(fragmentQueue.poll());
            } else {
                fragmentList.add(new FridgeListFragment());
            }
        }
        while(fragmentList.size() > stores.size() + 1){
            FridgeListFragment fragment = fragmentList.remove(stores.size() + 1 );
            fragmentQueue.add(fragment);
        }


        for(int i = 0 ; i < stores.size(); i++){
            ArrayList<FridgeItem> fridgeItems = new ArrayList<>(stores.get(i).getItems());

            List<FridgeItem> toDelete = new ArrayList<>();
            for(FridgeItem item : fridgeItems){
                if(item.getQuantity() == 0){
                    toDelete.add(item);
                }
                if(item.getQuantity() < item.getMinQuantity()){
                    buyStore.getItems().add(item);
                }
            }

            fridgeItems.removeAll(toDelete);

            addStoreFragment(stores.get(i), fridgeItems, i);
        }

        addStoreFragment(buyStore, buyStore.getItems(), stores.size());

        if(pagerAdapter != null) {
            try {
                pagerAdapter.notifyDataSetChanged();
            } catch (Exception e){
              e.printStackTrace();
            }
        }
    }

    public void addStoreFragment(Store store, List<FridgeItem> items, int position){
        ArrayList<FridgeItem> fridgeItems = new ArrayList<>(items);

        List<FridgeItem> toDelete = new ArrayList<>();
        if (query != null && query.length() > 0) {
            for (FridgeItem item : fridgeItems) {
                if (!item.getName().toLowerCase().contains(query)) {
                    toDelete.add(item);
                }
            }
        }

        fridgeItems.removeAll(toDelete);

        Collections.sort(fridgeItems);

        fragmentList.get(position).setData(fridgeItems, store);
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
            pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), (List<TitleFragment>) (List<?>) fragmentList);
            pager.setAdapter(pagerAdapter);
        }
        return view;
    }

    public int currentView(){
        return pager.getCurrentItem();
    }
}
