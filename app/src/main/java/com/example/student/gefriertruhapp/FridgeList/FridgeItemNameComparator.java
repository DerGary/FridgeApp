package com.example.student.gefriertruhapp.FridgeList;

import com.example.student.gefriertruhapp.Model.FridgeItem;

import java.util.Comparator;

/**
 * Created by Stefan on 12-10-16.
 */

public class FridgeItemNameComparator implements Comparator<FridgeItem> {
    @Override
    public int compare(FridgeItem lhs, FridgeItem rhs) {
        if(lhs == rhs) {
            return 0;
        }
        return lhs.getName().compareTo(rhs.getName());
    }
}