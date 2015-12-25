package com.example.student.gefriertruhapp.Model;

import org.joda.time.DateTime;

/**
 * Created by student on 21.12.15.
 */
public class ShelfItem extends FridgeItem {
    private int minQuantity;

    public ShelfItem(int id, String name, int quantity, DateTime notificationDate, String barCode, String notes, int minQuantity) {
        super(id, name, quantity, notificationDate, barCode, notes);
        this.minQuantity = minQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }
}
