package com.example.student.gefriertruhapp.Model;

import org.joda.time.DateTime;

/**
 * Created by student on 21.12.15.
 */
public class FridgeItem implements Comparable<FridgeItem> {
    private String name;
    private int quantity;
    private DateTime notificationDate;
    private String barCode;

    public FridgeItem(String name, int quantity, DateTime notificationDate, String barCode) {
        this.name = name;
        this.quantity = quantity;
        this.notificationDate = notificationDate;
        this.barCode = barCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if(quantity < 0){
            this.quantity = 0;
        }else {
            this.quantity = quantity;
        }
    }

    public DateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(DateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public int compareTo(FridgeItem another) {
        return notificationDate.compareTo(another.getNotificationDate());
    }
}
