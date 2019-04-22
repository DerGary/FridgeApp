package com.example.student.gefriertruhapp.FridgeList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.student.gefriertruhapp.Helper.TitleFragment;
import com.example.student.gefriertruhapp.Helper.TitleFragmentViewPagerAdapter;
import com.example.student.gefriertruhapp.Model.Category;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.OnPropertyChangedListener;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Model.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stefan on 21-05-15.
 */
public class FridgeListViewPagerFragment extends Fragment implements ViewPager.OnPageChangeListener, OnPropertyChangedListener {
    private ViewPager pager;
    private TitleFragmentViewPagerAdapter pagerAdapter;
    private View view;
    private List<FridgeListFragment> fragmentList = new ArrayList<FridgeListFragment>();
    private String query;
    private Category category;
    private Queue<FridgeListFragment> fragmentQueue = new LinkedList<>();
    private PagerTabStrip pagerTabStrip;
    private ItemMarkedListener markedListener;


    public FridgeListViewPagerFragment() {
        // Required empty public constructor
        allSorts[Sort.DateAscending.getValue()] = Sort.DateAscending;
        allSorts[Sort.DateDescending.getValue()] = Sort.DateDescending;
        allSorts[Sort.NameAscending.getValue()] = Sort.NameAscending;
        allSorts[Sort.NameDescending.getValue()] = Sort.NameDescending;
    }
    public void setMarkedListener(ItemMarkedListener markedListener){
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

    public void setCategoryQuery(Category category){
        this.category = category;
        setData();
        setActionBar();
    }

    public boolean onBackPressed(){
        if(query != null){
            setSearchQuery(null);
            return true;
        }
        if(category != null){
            setCategoryQuery(null);
            return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if(category != null){
                setCategoryQuery(null);
            }
            if(query != null){
                setSearchQuery(null);
            }
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
    Timer timer;
    @Override
    public void onPropertyChanged(String name) {
        if("linkedItems".equals(name) || "minQuantity".equals(name) || "quantity".equals(name)) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            final Context context = this.getActivity();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(context.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            setData();
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }
            }, 10);
        }
    }

    public enum Sort{
        DateAscending(0), DateDescending(1), NameAscending(2), NameDescending(3);
        private final int value;

        private Sort(int value) {
            this.value = value;
        }

        public String getText(Context context){
            if(value == DateAscending.value){
                return context.getString(R.string.date_ascending);
            }else if(value == DateDescending.value){
                return context.getString(R.string.date_descending);
            }else if(value == NameAscending.value){
                return context.getString(R.string.name_ascending);
            }else if(value == NameDescending.value){
                return context.getString(R.string.name_descending);
            }
            return "";
        }
        public int getValue(){
            return value;
        }
    }
    Sort currentSort = Sort.DateAscending;
    Sort[] allSorts = new Sort[4];

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_sort);
        String[] strings = new String[4];
        strings[Sort.DateAscending.getValue()] = Sort.DateAscending.getText(getActivity());
        strings[Sort.DateDescending.getValue()] = Sort.DateDescending.getText(getActivity());
        strings[Sort.NameAscending.getValue()] = Sort.NameAscending.getText(getActivity());
        strings[Sort.NameDescending.getValue()] = Sort.NameDescending.getText(getActivity());
        itemPos = currentSort.getValue();
        builder.setSingleChoiceItems(strings, itemPos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemPos = which;
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
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
        } else if(category != null) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("\"" + category.getName() + "\"");
        } else {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @SuppressWarnings("unchecked")
    public void setData() {
        ArrayList<Store> stores = new ArrayList<>(DataBaseSingleton.getInstance().getStores());
        for(Store store : stores){
            for(FridgeItem item : store.getItems()){
                item.subscribe(this);
            }
        }

        Store buyStore = new Store(getString(R.string.shopping_list), "");
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
                int quantityOfAllLinkedItems = item.getQuantity();
                if(item.getLinkedItems() != null){
                    for(FridgeItem linkedItem : item.getLinkedItems()){
                        quantityOfAllLinkedItems += linkedItem.getQuantity();
                    }
                }
                if(quantityOfAllLinkedItems == 0){
                    toDelete.add(item);
                }
                if(quantityOfAllLinkedItems < item.getMinQuantity()){
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

    public void addStoreFragment(Store store, List<FridgeItem> items, int position, ItemMarkedListener markedListener){
        ArrayList<FridgeItem> fridgeItems = new ArrayList<>(items);

        List<FridgeItem> toDelete = new ArrayList<>();
        if (query != null && query.length() > 0) {
            for (FridgeItem item : fridgeItems) {
                if (!item.getName().toLowerCase().contains(query)) {
                    toDelete.add(item);
                }
            }
        }
        if(category != null){
            for(FridgeItem item : fridgeItems){
                if(item.getCategory() != category){
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for(Store store : DataBaseSingleton.getInstance().getStores()){
            for(FridgeItem item : store.getItems()){
                item.unsubscribe(this);
            }
        }
    }
}
