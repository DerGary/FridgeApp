package com.example.student.gefriertruhapp.Model;

import org.joda.time.DateTime;

/**
 * Created by student on 21.12.15.
 */
public class ShelfItem extends FridgeItem {
    private int minQuantity;

    public ShelfItem(String _name, int _quantity, DateTime _notificationDate, String barCode, int minQuantity) {
        super(_name, _quantity, _notificationDate, barCode);
        this.minQuantity = minQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }
}
