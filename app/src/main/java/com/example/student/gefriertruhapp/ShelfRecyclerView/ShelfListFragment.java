package com.example.student.gefriertruhapp.ShelfRecyclerView;

import android.app.Fragment;
import android.content.Context;
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
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class ShelfListFragment extends TitleFragment implements ItemClickListener {
    private static final String TAG = "EventListFragment";
    private List<ShelfItem> _shelfItems;
    private GregorianCalendar _calendar;
    private Context _context;
    private View _view;
    private final String TITLE = "Lager";

    public ShelfListFragment() {
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
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(TITLE);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((Dashboard) getActivity()).get_menu().findItem(R.id.add).setVisible(true);
    }

    public void setData(List<ShelfItem> items) {
        _shelfItems = items;
        if(_view != null){
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
        _view = view;
        setAdapter();
        return view;
    }

    private void setAdapter(){
        RecyclerView listView = (RecyclerView) _view.findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter = new ShelfRecycleViewAdapter(_shelfItems, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Object data) {
//        EventDetailFragment detailFragment = new EventDetailFragment();
//        detailFragment.set_actualEvent(data);
//        getFragmentManager().beginTransaction().setCustomAnimations(
//                R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right
//        ).replace(R.id.main_layout, detailFragment).addToBackStack(null).commit();
    }

    public String getTitle() {
        return TITLE;
    }

    public void set_context(Context _context) {
        this._context = _context;
    }

}
