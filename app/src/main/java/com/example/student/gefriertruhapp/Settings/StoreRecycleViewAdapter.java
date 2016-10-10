package com.example.student.gefriertruhapp.Settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.ShelfRecyclerView.FridgeViewHolder;
import com.example.student.gefriertruhapp.ShelfRecyclerView.ItemClickListener;

import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */
public class StoreRecycleViewAdapter extends RecyclerView.Adapter<StoreViewHolder> {
    private List<Store> _list;
    private ItemClickListener _clickListener;

    public StoreRecycleViewAdapter(List<Store> list, ItemClickListener clickListener) {
        _list = list;
        this._clickListener = clickListener;
    }


    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new StoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StoreViewHolder holder, int position) {
        holder.assignData(_list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _clickListener.onItemClick(holder.get_data());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.store_item;
    }

    @Override
    public int getItemCount() {
        if(_list == null){
            return 0;
        }
        return _list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
