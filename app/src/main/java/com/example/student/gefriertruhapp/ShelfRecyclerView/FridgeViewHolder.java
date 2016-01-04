package com.example.student.gefriertruhapp.ShelfRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Stefan on 18-05-15.
 */
public class FridgeViewHolder extends RecyclerView.ViewHolder{
    public static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
    protected final TextView name, date, quantity;
    protected FridgeItem _data;

    public FridgeViewHolder(View itemView) {
        super(itemView);
        name = ((TextView) itemView.findViewById(R.id.item_name));
        date = ((TextView) itemView.findViewById(R.id.item_date));
        quantity = ((TextView) itemView.findViewById(R.id.item_quantity));
    }

    public void assignData(FridgeItem data) {
        _data = data;
        this.name.setText(data.getName());
        if(data.getNotificationDate() != null) {
            this.date.setText(formatter.print(data.getNotificationDate()));
        }else{
            this.date.setText("");
        }
        this.quantity.setText(data.getQuantity() + " / " + data.getMinQuantity());
    }
    public FridgeItem get_data() {
        return _data;
    }
}
