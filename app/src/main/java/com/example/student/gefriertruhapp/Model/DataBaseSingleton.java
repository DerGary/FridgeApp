package com.example.student.gefriertruhapp.Model;

import android.content.Context;
import android.os.SystemClock;

import com.example.student.gefriertruhapp.Notifications.NotificationBroadCastReceiver;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Julius on 25.06.2015.
 */
public class DataBaseSingleton {
    private static DataBaseSingleton ourInstance;
    private HashMap<String, FridgeItem> fridgeItems;
    private HashMap<String, ShelfItem> shelfItems;
    private HashMap<Integer, FridgeItem> itemIDs;
    private boolean loaded = false;
    private Context context;
    private final static String FRIDGE_ITEMS= "FRIDGE_ITEMS";
    private final static String SHELF_ITEMS = "SHELF_ITEMS";

    public ArrayList<FridgeItem> getFridgeList() {
        return new ArrayList<>(fridgeItems.values());
    }

    public ArrayList<ShelfItem> getShelfList() {
        return new ArrayList<>(shelfItems.values());
    }
    public static void init(Context context){
        if(ourInstance != null){
            return;
        }
        ourInstance = new DataBaseSingleton();
        ourInstance.context = context;
    }
    public static DataBaseSingleton getInstance() {
        return ourInstance;
    }

    private DataBaseSingleton() {
        this.shelfItems = new HashMap<>();
        this.fridgeItems = new HashMap<>();
        this.itemIDs = new HashMap<>();
    }

    public ShelfItem getShelfItem(String barCode){
        return shelfItems.get(barCode);
    }


    public FridgeItem getItemByID(int id){
        FridgeItem item = itemIDs.get(id);
        if(item == null){
            item = itemIDs.get(id);
        }
        return item;
    }

    public FridgeItem getFridgeItem(String barCode){
        return fridgeItems.get(barCode);
    }

    public void saveFridgeItem(FridgeItem item){
        this.fridgeItems.put(item.getBarCode(), item);
        this.itemIDs.put(item.getId(), item);
        if(item.getNotificationDate() != null && item.getNotificationDate().getMillis() > SystemClock.elapsedRealtime()){
            item.setNotified(false);
        }
        NotificationBroadCastReceiver.registerAlarm(context, item);
    }

    public void deleteShelfItem(ShelfItem item){
        this.shelfItems.remove(item.getBarCode());
        this.itemIDs.remove(item.getId());
        NotificationBroadCastReceiver.unregisterAlarm(context, item);
    }

    public void deleteFridgeItem(FridgeItem item){
        this.fridgeItems.remove(item.getBarCode());
        this.itemIDs.remove(item.getId());
        NotificationBroadCastReceiver.unregisterAlarm(context, item);
    }

    public void saveShelfItem(ShelfItem item){
        this.shelfItems.put(item.getBarCode(), item);
        this.itemIDs.put(item.getId(), item);
        if(item.getNotificationDate() != null && item.getNotificationDate().getMillis() > SystemClock.elapsedRealtime()){
            item.setNotified(false);
        }
        NotificationBroadCastReceiver.registerAlarm(context, item);
    }

    public void saveDataBase() {
        SharedPrefManager manager = new SharedPrefManager(context);
        manager.save(FRIDGE_ITEMS, getFridgeList());
        manager.save(SHELF_ITEMS, getShelfList());
    }

    public void loadDataBase() {
        if (loaded)
            return;

        SharedPrefManager manager = new SharedPrefManager(context);

        Type listType = new TypeToken<ArrayList<FridgeItem>>() {}.getType();
        ArrayList<FridgeItem> fridgeItems = manager.load(FRIDGE_ITEMS, listType);
        listType = new TypeToken<ArrayList<ShelfItem>>() {}.getType();
        ArrayList<ShelfItem> shelfItems = manager.load(SHELF_ITEMS, listType);
        this.shelfItems.clear();
        this.fridgeItems.clear();
        this.itemIDs.clear();
        if(fridgeItems != null) {
            for (FridgeItem item : fridgeItems) {
                this.fridgeItems.put(item.getBarCode(), item);
                this.itemIDs.put(item.getId(), item);
            }
        }
        if(shelfItems != null) {
            for (ShelfItem item : shelfItems) {
                this.shelfItems.put(item.getBarCode(), item);
                this.itemIDs.put(item.getId(), item);
            }
        }
        loaded = true;
    }
}
