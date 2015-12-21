package com.example.student.gefriertruhapp.ShelfRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student.gefriertruhapp.Model.ShelfItem;

/**
 * Created by Stefan on 18-05-15.
 */
public class ShelfViewHolder extends RecyclerView.ViewHolder {
    private final TextView _name, _minQuantity, _quantity;
    private ShelfItem _data;

    public ShelfViewHolder(View itemView) {
        super(itemView);
        _name = ((TextView) itemView.findViewById(R.id.event_lecture_name));
        _minQuantity = ((TextView) itemView.findViewById(R.id.event_lecture_type));
        _quantity = ((TextView) itemView.findViewById(R.id.event_docent));
    }

    public void assignData(ShelfItem data) {
        _data = data;
        this._name.setText(data.get_name());
        this._minQuantity.setText(data.get_minQuantity());
        this._quantity.setText(data.get_currentQuantity());

    }

    public ShelfItem get_data() {
        return _data;
    }

}
