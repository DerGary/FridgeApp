package com.example.student.gefriertruhapp.Helper;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.R;

/**
 * Created by Stefan on 22-07-16.
 */
public abstract class RecyclerListFragment extends TitleFragment {
    protected View view;
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
    protected abstract void setAdapter();
}