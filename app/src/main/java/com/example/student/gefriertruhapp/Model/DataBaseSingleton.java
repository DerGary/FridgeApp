package com.example.student.gefriertruhapp.Model;

import android.content.Context;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Julius on 25.06.2015.
 */
public class DataBaseSingleton {
    private static DataBaseSingleton ourInstance = new DataBaseSingleton();
    private HashMap<String, FridgeItem> _fridgeList;
    private HashMap<String, ShelfItem> _shelfList;
    private static boolean loaded = false;

    private final static String FRIDGE_ITEMS= "FRIDGE_ITEMS";
    private final static String SHELF_ITEMS = "SHELF_ITEMS";

    public ArrayList<FridgeItem> get_fridgeList() {
        return new ArrayList<>(_fridgeList.values());
    }

    public ArrayList<ShelfItem> get_shelfList() {
        return new ArrayList<>(_shelfList.values());
    }

    public static DataBaseSingleton getInstance() {
        return ourInstance;
    }

    private DataBaseSingleton() {
        this._shelfList = new HashMap<>();
        this._fridgeList = new HashMap<>();
    }

    public ShelfItem getShelfItem(String barCode){
        return _shelfList.get(barCode);
    }

    public FridgeItem getFridgeItem(String barCode){
        return _fridgeList.get(barCode);
    }

    public void saveFridgeItem(FridgeItem item){
        this._fridgeList.put(item.getBarCode(), item);
    }

    public void deleteShelfItem(ShelfItem item){
        this._shelfList.remove(item.getBarCode());
    }

    public void deleteFridgeItem(FridgeItem item){
        this._fridgeList.remove(item.getBarCode());
    }

    public void saveShelfItem(ShelfItem item){
        this._shelfList.put(item.getBarCode(), item);
    }

    public void saveDataBase(Context context) {
        SharedPrefManager manager = new SharedPrefManager(context);
        manager.save(FRIDGE_ITEMS, get_fridgeList());
        manager.save(SHELF_ITEMS, get_shelfList());
    }

    public void loadDataBase(Context context) {
        if (loaded)
            return;

        SharedPrefManager manager = new SharedPrefManager(context);

        Type listType = new TypeToken<ArrayList<FridgeItem>>() {}.getType();
        ArrayList<FridgeItem> fridgeItems = manager.load(FRIDGE_ITEMS, listType);
        listType = new TypeToken<ArrayList<ShelfItem>>() {}.getType();
        ArrayList<ShelfItem> shelfItems = manager.load(SHELF_ITEMS, listType);
        _shelfList.clear();
        _fridgeList.clear();
        if(fridgeItems != null) {
            for (FridgeItem item : fridgeItems) {
                _fridgeList.put(item.getBarCode(), item);
            }
        }
        if(shelfItems != null) {
            for (ShelfItem item : shelfItems) {
                _shelfList.put(item.getBarCode(), item);
            }
        }
        loaded = true;
    }
}
