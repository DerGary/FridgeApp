package com.example.student.gefriertruhapp.Settings;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;

/**
 * Created by Stefan on 22-07-16.
 */
public class StoreViewHolder extends RecyclerView.ViewHolder {
    protected final TextView name;
    protected Store _data;

    public StoreViewHolder(View itemView) {
        super(itemView);

        name = ((TextView) itemView.findViewById(R.id.store_name));
    }
    public void assignData(Store data) {
        _data = data;
        this.name.setText(data.getName());
    }
    public Store get_data() {
        return _data;
    }
}
