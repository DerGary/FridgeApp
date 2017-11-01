package com.example.student.gefriertruhapp.DoInventory;

import android.view.View;

import com.example.student.gefriertruhapp.FridgeList.ViewHolderBuilder;
import com.example.student.gefriertruhapp.R;

/**
 * Created by stefa on 01-Nov-17.
 */
public class DoInventoryFridgeItemViewHolderBuilder implements ViewHolderBuilder<DoInventoryFridgeItemViewHolder> {

    @Override
    public DoInventoryFridgeItemViewHolder build(View view) {
        return new DoInventoryFridgeItemViewHolder(view);
    }

    @Override
    public int getLayout() {
        return R.layout.do_inventory_list_item;
    }
}
