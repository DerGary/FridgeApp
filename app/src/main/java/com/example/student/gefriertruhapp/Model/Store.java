package com.example.student.gefriertruhapp.Model;

import com.example.student.gefriertruhapp.Model.FridgeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */
public class Store {
    private String name;
    private String description;
    private transient List<FridgeItem> items;

    public Store(String name, String description) {
        this.name = name;
        this.description = description;
        items = new ArrayList<>();
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

}
