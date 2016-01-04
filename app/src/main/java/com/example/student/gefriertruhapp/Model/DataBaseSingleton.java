package com.example.student.gefriertruhapp.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.text.TextUtils;

import com.example.student.gefriertruhapp.Helper.ExtendedGson;
import com.example.student.gefriertruhapp.Helper.FileAccess;
import com.example.student.gefriertruhapp.Helper.StorageException;
import com.example.student.gefriertruhapp.Notifications.NotificationBroadCastReceiver;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Julius on 25.06.2015.
 */
public class DataBaseSingleton {
    private static DataBaseSingleton ourInstance;
    private HashMap<String, List<FridgeItem>> fridgeItems;
    private HashMap<String, List<ShelfItem>> shelfItems;
    private HashMap<Integer, FridgeItem> itemIDs;
    private boolean loaded = false;
    private Context context;
    private final static String FRIDGE_ITEMS= "FRIDGE_ITEMS";
    private final static String SHELF_ITEMS = "SHELF_ITEMS";

    public ArrayList<FridgeItem> getFridgeList() {
        ArrayList<FridgeItem> list = new ArrayList<>();
        for (Map.Entry<Integer, FridgeItem> kv : itemIDs.entrySet()) {
            if (!(kv.getValue() instanceof ShelfItem)) {
                list.add(kv.getValue());
            }
        }
        return list;
    }

    public ArrayList<ShelfItem> getShelfList() {
        ArrayList<ShelfItem> list = new ArrayList<>();
        for (Map.Entry<Integer, FridgeItem> kv : itemIDs.entrySet()) {
            if (kv.getValue() instanceof ShelfItem) {
                list.add((ShelfItem)kv.getValue());
            }
        }
        return list;
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

    public List<ShelfItem> getShelfItems(String barCode){
        return shelfItems.get(barCode);
    }


    public FridgeItem getItemByID(int id){
        FridgeItem item = itemIDs.get(id);
        if(item == null){
            item = itemIDs.get(id);
        }
        return item;
    }

    public List<FridgeItem> getFridgeItems(String barCode){
        return fridgeItems.get(barCode);
    }

    public void saveItem(FridgeItem item){
        if(item instanceof ShelfItem){
            saveShelfItem((ShelfItem) item);
        }else{
            saveFridgeItem(item);
        }
    }

    public void deleteItem(FridgeItem item){
        if(item instanceof ShelfItem){
            deleteShelfItem((ShelfItem)item);
        }else{
            deleteFridgeItem(item);
        }
    }

    public void saveFridgeItem(FridgeItem item){
        if(item.getBarCode() != null) {
            List<FridgeItem> list = this.fridgeItems.get(item.getBarCode());
            if(list == null){
                list = new ArrayList<FridgeItem>();
            }
            list.remove(item);
            list.add(item);
            this.fridgeItems.put(item.getBarCode(), list);
        }
        this.itemIDs.put(item.getId(), item);
        if(item.getNotificationDate() != null && item.getNotificationDate().getMillis() > SystemClock.elapsedRealtime()){
            item.setNotified(false);
        }
        NotificationBroadCastReceiver.registerAlarm(context, item);
    }

    public void deleteShelfItem(ShelfItem item){
        if(item.getBarCode() != null) {
            List<ShelfItem> list = this.shelfItems.get(item.getBarCode());
            if(list != null){
                list.remove(item);
            }
        }
        this.itemIDs.remove(item.getId());
        NotificationBroadCastReceiver.unregisterAlarm(context, item);
    }

    public void deleteFridgeItem(FridgeItem item){
        if(item.getBarCode() != null) {
            List<FridgeItem> list = this.fridgeItems.get(item.getBarCode());
            if(list != null){
                list.remove(item);
            }
        }
        this.itemIDs.remove(item.getId());
        NotificationBroadCastReceiver.unregisterAlarm(context, item);
    }

    public void saveShelfItem(ShelfItem item){
        if(item.getBarCode() != null) {
            List<ShelfItem> list = this.shelfItems.get(item.getBarCode());
            if(list == null){
                list = new ArrayList<ShelfItem>();
            }
            list.remove(item);
            list.add(item);
            this.shelfItems.put(item.getBarCode(), list);
        }
        this.itemIDs.put(item.getId(), item);
        if(item.getNotificationDate() != null && item.getNotificationDate().getMillis() > SystemClock.elapsedRealtime()){
            item.setNotified(false);
        }
        NotificationBroadCastReceiver.registerAlarm(context, item);
    }

    public void saveDataBase() {
        Gson gson = ExtendedGson.getInstance();
        String json = gson.toJson(getShelfList());
        try {
            FileAccess.writeToStorage(json, SHELF_ITEMS);
            json = gson.toJson(getFridgeList());
            FileAccess.writeToStorage(json, FRIDGE_ITEMS);
        }
        catch (StorageException ex){
            showStorageError();
        }
    }



    public void loadDataBase() {
        if (loaded)
            return;

        this.shelfItems.clear();
        this.fridgeItems.clear();
        this.itemIDs.clear();

        int biggestID = -1;

        try {
            Gson gson = ExtendedGson.getInstance();
            String json = FileAccess.readFromStorage(FRIDGE_ITEMS);
            ArrayList<FridgeItem> fridgeItems = null;
            ArrayList<ShelfItem> shelfItems = null;
            if (json != null) {
                Type listType = new TypeToken<ArrayList<FridgeItem>>() {
                }.getType();
                fridgeItems = gson.fromJson(json, listType);
                if (fridgeItems != null) {
                    for (FridgeItem item : fridgeItems) {
                        if (item.getBarCode() != null) {
                            List<FridgeItem> list = this.fridgeItems.get(item.getBarCode());
                            if(list == null){
                                list = new ArrayList<>();
                            }
                            list.add(item);
                            this.fridgeItems.put(item.getBarCode(), list);
                        }
                        this.itemIDs.put(item.getId(), item);
                        if(item.getId() > biggestID){
                            biggestID = item.getId();
                        }
                    }
                }
            }
            json = FileAccess.readFromStorage(SHELF_ITEMS);
            if (json != null) {
                Type listType = new TypeToken<ArrayList<ShelfItem>>() {
                }.getType();
                shelfItems = gson.fromJson(json, listType);
                if (shelfItems != null) {
                    for (ShelfItem item : shelfItems) {
                        if (item.getBarCode() != null) {
                            List<ShelfItem> list = this.shelfItems.get(item.getBarCode());
                            if(list == null){
                                list = new ArrayList<>();
                            }
                            list.add(item);
                            this.shelfItems.put(item.getBarCode(), list);
                        }
                        this.itemIDs.put(item.getId(), item);
                        if(item.getId() > biggestID){
                            biggestID = item.getId();
                        }
                    }
                }
            }
        }
        catch (StorageException ex){
            showStorageError();
        }
        SharedPrefManager manager =  new SharedPrefManager(context);
        manager.saveNewID(biggestID);
        loaded = true;
    }
    public void showStorageError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Speicher fehlt");
        builder.setMessage("Der Speicher konnte nicht gefunden werden. Wenn das Handy an den Computer angesteckt ist, muss es abgesteckt werden, oder eine Speicherkarte eingelegt werden, damit die App auf die gespeicherten Daten zugreifen kann.");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(-1);
            }
        });
        builder.create();
    }
}
