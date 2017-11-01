package com.example.student.gefriertruhapp.FridgeList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Model.FridgeItem;

import java.util.List;

/**
 * Created by Stefan on 18-05-15.
 */
public class FridgeRecycleViewAdapter<T extends FridgeItemRecyclerViewHolderBase> extends RecyclerView.Adapter<T>{
    private List<FridgeItem> list;
    private ItemClickListener clickListener;
    private ItemMarkedListener markedListener;
    private ViewHolderBuilder<T> viewHolderBuilder;

    public FridgeRecycleViewAdapter(ViewHolderBuilder<T> viewHolderBuilder, List<FridgeItem> list, ItemClickListener clickListener, ItemMarkedListener markedListener) {
        this.list = list;
        this.clickListener = clickListener;
        this.markedListener = markedListener;
        this.viewHolderBuilder = viewHolderBuilder;
    }


    public void setData(List<FridgeItem> list, ItemClickListener clickListener, ItemMarkedListener markedListener){
        this.list = list;
        this.clickListener = clickListener;
        this.markedListener = markedListener;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return viewHolderBuilder.build(v);
    }

    @Override
    public void onBindViewHolder(final T holder, int position) {
        holder.assignData(list.get(position));
        holder.setClickListener(clickListener);
        holder.setMarkedListener(markedListener);
    }

    @Override
    public int getItemViewType(int position) {
        return viewHolderBuilder.getLayout();
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
