package com.example.student.gefriertruhapp.Model;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;

import com.example.student.gefriertruhapp.R;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FridgeItem extends SupportsChangedEvents implements Comparable<FridgeItem> {
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private int quantity;
    @Expose
    private DateTime notificationDate;
    @Expose
    private String barCode;
    @Expose
    private String notes;
    @Expose
    private boolean notified = false;
    @Expose
    private int minQuantity;
    private transient Store store;
    private transient Iterable<FridgeItem> linkedItems;
    private transient boolean isMarked;
    private transient int gotQuantity;
    private transient Category category;
    @Expose
    private int categoryId;
    @Expose
    private List<Integer> linkedItemIds;


    public FridgeItem(int id, String name, int quantity, DateTime notificationDate, String barCode, String notes, boolean notified, int minQuantity, Store store, Category category, int categoryId, List<Integer> linkedItemIds, Iterable<FridgeItem> linkedItems) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.notificationDate = notificationDate;
        this.barCode = barCode;
        this.notes = notes;
        this.notified = notified;
        this.minQuantity = minQuantity;
        this.store = store;
        this.category = category;
        this.categoryId = categoryId;
        this.linkedItemIds = linkedItemIds;
        this.linkedItems = linkedItems;
    }
    public FridgeItem(FridgeItem item){
        this(item.id, item.name, item.quantity, item.notificationDate, item.barCode, item.notes, item.isNotified(), item.minQuantity, item.store, item.getCategory(), item.getCategoryId(), item.getLinkedItemIds(), item.getLinkedItems());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        OnPropertyChanged("name");
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
        OnPropertyChanged("quantity");
    }

    public DateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(DateTime notificationDate) {
        this.notificationDate = notificationDate;
        OnPropertyChanged("notificationDate");
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
        OnPropertyChanged("barCode");
    }

    public String getNotes() {
        return notes;
    }
    public String getNotesOrPlaceholderIfEmpty(Context context){
        if(notes == null || notes.isEmpty()){
            return context.getString(R.string.no_notes);
        }
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        OnPropertyChanged("notes");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        OnPropertyChanged("id");
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
        OnPropertyChanged("notified");
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
        OnPropertyChanged("minQuantity");
    }

    public String getNotificationDateString(Context context){
        if(getNotificationDate() == null){
            return context.getString(R.string.no_date);
        }
        return formatter.print(getNotificationDate());
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
        OnPropertyChanged("store");
    }

    public Iterable<FridgeItem> getLinkedItems() {
        return linkedItems;
    }

    public void setLinkedItems(Iterable<FridgeItem> linkedItems) {
        this.linkedItems = linkedItems;
        linkedItemIds = new ArrayList<Integer>();
        if(linkedItems != null) {
            for (FridgeItem item : linkedItems) {
                linkedItemIds.add(item.getId());
            }
        }
        OnPropertyChanged("linkedItems");
    }


    public List<Integer> getLinkedItemIds(){
        if(linkedItems != null){
            List<Integer> list = new ArrayList<Integer>();
            for (FridgeItem item : linkedItems) {
                list.add(item.getId());
            }
            return list;
        }
        return linkedItemIds;
    }
    public void setLinkedItemIds(List<Integer> list){
        linkedItemIds = list;
    }

    public void setMarked(boolean isMarked){
        this.isMarked = isMarked;
        OnPropertyChanged("isMarked");
    }
    public boolean isMarked(){
        return isMarked;
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

    public int getGotQuantity() {
        return gotQuantity;
    }

    public void setGotQuantity(int gotQuantity) {
        this.gotQuantity = gotQuantity;
        OnPropertyChanged("gotQuantity");
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
