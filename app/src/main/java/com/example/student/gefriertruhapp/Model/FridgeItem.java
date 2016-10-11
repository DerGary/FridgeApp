package com.example.student.gefriertruhapp.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.student.gefriertruhapp.Settings.Store;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by student on 21.12.15.
 */
public class FridgeItem implements Comparable<FridgeItem> {
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
    private int id;
    private String name;
    private int quantity;
    private DateTime notificationDate;
    private String barCode;
    private String notes;
    private boolean notified = false;
    private int minQuantity;
    private transient Store store;

    public FridgeItem(int id, String name, int quantity, DateTime notificationDate, String barCode, String notes, int minQuantity, Store store) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.notificationDate = notificationDate;
        this.barCode = barCode;
        this.notes = notes;
        this.minQuantity = minQuantity;
        this.store = store;
    }
    public FridgeItem(FridgeItem item){
        this(item.id, item.name, item.quantity, item.notificationDate, item.barCode, item.notes, item.minQuantity, item.store);
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
    public int compareTo(@NonNull FridgeItem another) {
        if(this == another) {
            return 0;
        }

        if(notificationDate != null && another.getNotificationDate() != null) {
            int compare = notificationDate.compareTo(another.getNotificationDate());
            if (compare == 0) {
                return name.compareTo(another.name);
            }
            return compare;
        } else if(notificationDate != null) {
            return -1;
        } else if(another.notificationDate != null) {
            return 1;
        } else {
            return name.compareTo(another.name);
        }
    }

    public String getNotes() {
        return notes;
    }
    public String getNotesOrPlaceholderIfEmpty(){
        if(notes == null || notes.isEmpty()){
            return "Keine Notiz";
        }
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getNotificationDateString(){
        if(getNotificationDate() == null){
            return "Kein Datum";
        }
        return formatter.print(getNotificationDate());
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
