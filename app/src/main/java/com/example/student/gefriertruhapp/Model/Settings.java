package com.example.student.gefriertruhapp.Model;

import com.example.student.gefriertruhapp.Model.Store;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */
public class Settings {
    @Expose
    private List<Store> stores;

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}
