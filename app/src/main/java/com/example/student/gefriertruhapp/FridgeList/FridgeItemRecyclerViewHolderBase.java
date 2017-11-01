package com.example.student.gefriertruhapp.FridgeList;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.OnPropertyChangedListener;

/**
 * Created by Stefan on 18-05-15.
 */
public abstract class FridgeItemRecyclerViewHolderBase extends RecyclerView.ViewHolder implements OnPropertyChangedListener, View.OnLongClickListener, View.OnClickListener{
    protected FridgeItem data;
    protected View itemView;
    protected ItemMarkedListener markedListener;
    protected ItemClickListener clickListener;

    public FridgeItemRecyclerViewHolderBase(View itemView) {
        super(itemView);
        this.itemView = itemView;
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void setMarkedListener(ItemMarkedListener listener){
        markedListener = listener;
    }
    public void setClickListener(ItemClickListener listener){
        clickListener = listener;
    }

    public void assignData(FridgeItem data) {
        if(this.data != null) {
            this.data.unsubscribe(this);
        }
        this.data = data;
        this.data.subscribe(this);
        populateViewWithData(this.data);
    }


    protected abstract void populateViewWithData(FridgeItem data);

    @Override
    public void onPropertyChanged(String name) {
        populateViewWithData(data);
    }

    @Override
    public abstract void onClick(View view);

    @Override
    public abstract boolean onLongClick(View view);
}
