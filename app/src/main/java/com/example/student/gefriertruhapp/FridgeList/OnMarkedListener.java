package com.example.student.gefriertruhapp.FridgeList;

import com.example.student.gefriertruhapp.Model.FridgeItem;

/**
 * Created by Stefan on 21-10-17.
 */

public interface OnMarkedListener {
    void setMarked(FridgeItem item);
    void setUnmarked(FridgeItem item);
}
