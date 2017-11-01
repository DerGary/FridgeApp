package com.example.student.gefriertruhapp.DoInventory;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.student.gefriertruhapp.FridgeList.ViewHolderBuilder;
import com.example.student.gefriertruhapp.FridgeList.FridgeItemRecyclerViewHolderBase;
import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DoInventoryFridgeItemViewHolder extends FridgeItemRecyclerViewHolderBase {
    public static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
    protected final TextView name, date, desiredQuantity, gotQuantity;

    public DoInventoryFridgeItemViewHolder(View view){
        super(view);
        name = (TextView)view.findViewById(R.id.do_inventory_item_name);
        date = (TextView)view.findViewById(R.id.do_inventory_item_date);
        desiredQuantity = (TextView)view.findViewById(R.id.do_inventory_item_desired_quantity);
        gotQuantity = (TextView)view.findViewById(R.id.do_inventory_item_got_quantity);
    }

    @Override
    protected void populateViewWithData(FridgeItem data) {
        this.name.setText(data.getName());
        if(data.getNotificationDate() != null) {
            if(data.getNotificationDate().isBeforeNow()){
                this.date.setTextColor(Color.RED);
            }else{
                this.date.setTextColor(Color.BLACK);
            }
            this.date.setText(formatter.print(data.getNotificationDate()));
        }else{
            this.date.setText("");
        }
        this.desiredQuantity.setText(Integer.toString(data.getQuantity()));
        this.gotQuantity.setText(Integer.toString(data.getGotQuantity()));
    }

    @Override
    public void onClick(View view) {
        clickListener.onItemClick(data);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
