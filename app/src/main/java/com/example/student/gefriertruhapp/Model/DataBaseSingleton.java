package com.example.student.gefriertruhapp.Model;

import android.content.Context;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;


import java.util.ArrayList;

/**
 * Created by Julius on 25.06.2015.
 */
public class DataBaseSingleton {
    private static DataBaseSingleton ourInstance = new DataBaseSingleton();
    private ArrayList<FridgeItem> _fridgeList;
    private ArrayList<ShelfItem> _shelfList;
    private static boolean loaded = false;

    public ArrayList<FridgeItem> get_fridgeList() {
        return _fridgeList;
    }

    public void set_fridgeList(ArrayList<FridgeItem> _fridgeList) {
        this._fridgeList = _fridgeList;
    }

    public ArrayList<ShelfItem> get_shelfList() {
        return _shelfList;
    }

    public void set_shelfList(ArrayList<ShelfItem> _shelfList) {
        this._shelfList = _shelfList;
    }

    public static DataBaseSingleton getInstance() {
        return ourInstance;
    }

    private DataBaseSingleton() {
        this._shelfList = new ArrayList<ShelfItem>();
        this._fridgeList = new ArrayList<FridgeItem>();
    }

    public ShelfItem getShelfItem(String barCode){
        for(ShelfItem item : _shelfList){
            if(item.get_barCode().equals(barCode)){
                return item;
            }
        }
        return null;
    }
    public FridgeItem getFridgeItem(String barCode){
        for(FridgeItem item : _fridgeList){
            if(item.get_barCode().equals(barCode)){
                return item;
            }
        }
        return null;
    }

    public void saveDataBase(Context context) {
        SharedPrefManager manager = new SharedPrefManager(context);
        manager.saveDataBase();
    }

    public void loadDataBase(Context context) {
        if (loaded)
            return;

        SharedPrefManager manager = new SharedPrefManager(context);
        manager.loadDataBase();
        loaded = true;
    }
}
