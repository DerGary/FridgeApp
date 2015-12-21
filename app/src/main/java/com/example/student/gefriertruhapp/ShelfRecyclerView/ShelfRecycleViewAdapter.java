package com.example.student.gefriertruhapp.ShelfRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.gefriertruhapp.Model.ShelfItem;

import java.util.List;

/**
 * Created by Stefan on 18-05-15.
 */
public class ShelfRecycleViewAdapter extends RecyclerView.Adapter<ShelfViewHolder>{
    private List<ShelfItem> _list;
    private ItemClickListener _clickListener;

    public ShelfRecycleViewAdapter(List<ShelfItem> list, ItemClickListener clickListener) {
        _list = list;
        this._clickListener = clickListener;
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ShelfViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShelfViewHolder holder, int position) {
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
        return R.layout.cardview_event_item;
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
