package com.example.student.gefriertruhapp.FridgeList;

import android.view.View;

import com.example.student.gefriertruhapp.R;

public class FridgeViewHolderBuilder implements ViewHolderBuilder<FridgeViewHolder> {

    @Override
    public FridgeViewHolder build(View view) {
        return new FridgeViewHolder(view);
    }

    @Override
    public int getLayout() {
        return R.layout.list_item;
    }
}
