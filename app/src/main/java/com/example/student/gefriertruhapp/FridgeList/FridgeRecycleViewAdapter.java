package com.example.student.gefriertruhapp.FridgeList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;

import java.util.List;

/**
 * Created by Stefan on 18-05-15.
 */
public class FridgeRecycleViewAdapter extends RecyclerView.Adapter<FridgeViewHolder>{
    private List<FridgeItem> list;
    private ItemClickListener clickListener;
    private OnMarkedListener markedListener;

    public FridgeRecycleViewAdapter(List<FridgeItem> list, ItemClickListener clickListener, OnMarkedListener markedListener) {
        this.list = list;
        this.clickListener = clickListener;
        this.markedListener = markedListener;
    }

    public void setData(List<FridgeItem> list, ItemClickListener clickListener, OnMarkedListener markedListener){
        this.list = list;
        this.clickListener = clickListener;
        this.markedListener = markedListener;
    }

    @Override
    public FridgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new FridgeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FridgeViewHolder holder, int position) {
        holder.assignData(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(holder.get_data());
            }
        });
        holder.setMarkedListener(markedListener);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_item;
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
