package com.example.student.gefriertruhapp.ViewPager;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unchecked")
    public void setData() {
        if(list.size() == 0) {
            list.add(new FridgeListFragment());
            list.add(new FridgeListFragment());
            list.add(new FridgeListFragment());
        }
        ArrayList<FridgeItem> fridgeItems = DataBaseSingleton.getInstance().get_fridgeList();
        Collections.sort(fridgeItems);
        ArrayList<ShelfItem> items = DataBaseSingleton.getInstance().get_shelfList();
        Collections.sort(items);
        ArrayList<ShelfItem> shoppingList = new ArrayList<>();
        for(ShelfItem item : items){
            if(item.getQuantity() < item.getMinQuantity()){
                shoppingList.add(item);
            }
        }

        list.get(0).setData(fridgeItems, "Gefriertruhe");
        list.get(1).setData((List<FridgeItem>) (List<?>) items, "Lager");
        list.get(2).setData((List<FridgeItem>) (List<?>) shoppingList, "Einkaufsliste");
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

}
