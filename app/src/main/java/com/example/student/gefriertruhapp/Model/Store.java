package com.example.student.gefriertruhapp.Model;

import android.graphics.Color;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */
public class Store {
    @Expose
    private String name;
    @Expose
    private String description;
    private transient List<FridgeItem> items;
    @Expose
    private int color = Color.rgb(0,0,0);

    public Store(String name, String description) {
        this.name = name;
        this.description = description;
        items = new ArrayList<>();
    }

    public Store(Store store){
        this(store.getName(), store.getDescription());
        items = store.getItems();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FridgeItem> getItems() {
        return items;
    }

    public void setItems(List<FridgeItem> items) {
        this.items = items;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
