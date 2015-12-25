package com.example.student.gefriertruhapp.ShelfRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;

import java.util.List;

/**
 * Created by Stefan on 18-05-15.
 */
public class FridgeRecycleViewAdapter extends RecyclerView.Adapter<FridgeViewHolder>{
    private List<FridgeItem> _list;
    private ItemClickListener _clickListener;

    public FridgeRecycleViewAdapter(List<FridgeItem> list, ItemClickListener clickListener) {
        _list = list;
        this._clickListener = clickListener;
    }

    @Override
    public FridgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new FridgeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FridgeViewHolder holder, int position) {
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
        return R.layout.list_item;
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
