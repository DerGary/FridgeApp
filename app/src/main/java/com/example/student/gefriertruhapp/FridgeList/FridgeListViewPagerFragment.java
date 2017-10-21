package com.example.student.gefriertruhapp.FridgeList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Helper.Action;
import com.example.student.gefriertruhapp.Helper.TitleFragment;
import com.example.student.gefriertruhapp.Helper.TitleFragmentViewPagerAdapter;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Model.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Stefan on 21-05-15.
 */
public class FridgeListViewPagerFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager pager;
    private TitleFragmentViewPagerAdapter pagerAdapter;
    private View view;
    private List<FridgeListFragment> fragmentList = new ArrayList<FridgeListFragment>();
    private String query;
    private Queue<FridgeListFragment> fragmentQueue = new LinkedList<>();
    private PagerTabStrip pagerTabStrip;
    private OnMarkedListener markedListener;


    public FridgeListViewPagerFragment() {
        // Required empty public constructor
        allSorts[Sort.DateAscending.getValue()] = Sort.DateAscending;
        allSorts[Sort.DateDescending.getValue()] = Sort.DateDescending;
        allSorts[Sort.NameAscending.getValue()] = Sort.NameAscending;
        allSorts[Sort.NameDescending.getValue()] = Sort.NameDescending;
    }
    public void setMarkedListener(OnMarkedListener markedListener){
        this.markedListener = markedListener;
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
        if(item.getItemId() == android.R.id.home) {
            setSearchQuery(null);
            return true;
        } else if(item.getItemId() == R.id.sort){
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    int itemPos = 0;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int color = fragmentList.get(position).getStore().getColor();
        pagerTabStrip.setTextColor(color);
        pagerTabStrip.setTabIndicatorColor(color);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public enum Sort{
        DateAscending(0, "Datum aufsteigend"), DateDescending(1, "Datum absteigend"), NameAscending(2, "Name aufsteigend"), NameDescending(3, "Name absteigend");
        private final int value;
        private final String text;

        private Sort(int value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getText(){
            return text;
        }
        public int getValue(){
            return value;
        }
    }
    Sort currentSort = Sort.DateAscending;
    Sort[] allSorts = new Sort[4];

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sortierung wählen");
        String[] strings = new String[4];
        strings[Sort.DateAscending.getValue()] = Sort.DateAscending.getText();
        strings[Sort.DateDescending.getValue()] = Sort.DateDescending.getText();
        strings[Sort.NameAscending.getValue()] = Sort.NameAscending.getText();
        strings[Sort.NameDescending.getValue()] = Sort.NameDescending.getText();
        itemPos = currentSort.getValue();
        builder.setSingleChoiceItems(strings, itemPos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemPos = which;
            }
        });
        builder.setNegativeButton("Abbrechen", null);
        builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentSort = allSorts[itemPos];
                setData();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        this.markedListener = markedListener;
        ArrayList<Store> stores = new ArrayList<>(DataBaseSingleton.getInstance().getStores());
        Store buyStore = new Store("Einkaufsliste", "");
        buyStore.setColor(Color.rgb(0,0,0));

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

            addStoreFragment(stores.get(i), fridgeItems, i, markedListener);
        }

        addStoreFragment(buyStore, buyStore.getItems(), stores.size(), markedListener);

        if(pagerAdapter != null) {
            try {
                pagerAdapter.notifyDataSetChanged();
            } catch (Exception e){
              e.printStackTrace();
            }
        }
        if (pagerTabStrip != null){
            int position = pager.getCurrentItem();
            int color = fragmentList.get(position).getStore().getColor();
            pagerTabStrip.setTextColor(color);
            pagerTabStrip.setTabIndicatorColor(color);
        }
    }

    public void addStoreFragment(Store store, List<FridgeItem> items, int position, OnMarkedListener markedListener){
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

        if(currentSort == Sort.DateAscending){
            Collections.sort(fridgeItems);
        } else if(currentSort == Sort.DateDescending){
            Collections.sort(fridgeItems, Collections.<FridgeItem>reverseOrder());
        }else if(currentSort == Sort.NameAscending){
            Collections.sort(fridgeItems, new FridgeItemNameComparator());
        }else if(currentSort == Sort.NameDescending){
            Collections.sort(fridgeItems, Collections.reverseOrder(new FridgeItemNameComparator()));
        }

        fragmentList.get(position).setData(fridgeItems, store, markedListener);
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
            pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_header);
            pager.addOnPageChangeListener(this);
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
