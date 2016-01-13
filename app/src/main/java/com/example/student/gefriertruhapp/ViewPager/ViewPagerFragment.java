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

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.ShelfRecyclerView.FridgeListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stefan on 21-05-15.
 */
public class ViewPagerFragment extends Fragment {
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    private View view;
    private List<FridgeListFragment> list = new ArrayList<FridgeListFragment>();
    private String query;

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
        if(list.size() == 0) {
            list.add(new FridgeListFragment());
            list.add(new FridgeListFragment());
            list.add(new FridgeListFragment());
        }
        ArrayList<FridgeItem> fridgeItems = DataBaseSingleton.getInstance().getFridgeList();
        Collections.sort(fridgeItems);
        ArrayList<ShelfItem> shelfItems = DataBaseSingleton.getInstance().getShelfList();
        Collections.sort(shelfItems);
        ArrayList<FridgeItem> shoppingList = new ArrayList<>();
        for(ShelfItem item : shelfItems){
            if(item.getQuantity() < item.getMinQuantity()){
                shoppingList.add(item);
            }
        }
        for(FridgeItem item : fridgeItems){
            if(item.getQuantity() < item.getMinQuantity()){
                shoppingList.add(item);
            }
        }

        if(query != null && query.length() > 0){
            List<FridgeItem> toDelete = new ArrayList<>();
            for(FridgeItem item : fridgeItems){
                if(!item.getName().toLowerCase().contains(query)){
                    toDelete.add(item);
                }
            }
            fridgeItems.removeAll(toDelete);
            List<ShelfItem> toDeleteShelf = new ArrayList<>();
            for(ShelfItem item : shelfItems){
                if(!item.getName().toLowerCase().contains(query)){
                    toDeleteShelf.add(item);
                }
            }
            shelfItems.removeAll(toDeleteShelf);
            shoppingList.removeAll(toDeleteShelf);
        }

        list.get(PageType.FridgeList.getI()).setData(fridgeItems, PageType.FridgeList.toString());
        list.get(PageType.ShelfList.getI()).setData((List<FridgeItem>) (List<?>) shelfItems, PageType.ShelfList.toString());
        list.get(PageType.ShoppingList.getI()).setData((List<FridgeItem>) (List<?>) shoppingList, PageType.ShoppingList.toString());
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
            pagerAdapter = new ViewPagerAdapter(getFragmentManager(), (List<TitleFragment>) (List<?>) list);
            pager.setAdapter(pagerAdapter);
        }
        return view;
    }

    public int currentView(){
        return pager.getCurrentItem();
    }
}
