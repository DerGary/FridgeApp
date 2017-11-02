package com.example.student.gefriertruhapp.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;

import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.History.HistoryHelper;
import com.example.student.gefriertruhapp.Serialization.ExtendedGson;
import com.example.student.gefriertruhapp.Serialization.FileAccess;
import com.example.student.gefriertruhapp.Serialization.StorageException;
import com.example.student.gefriertruhapp.Notifications.NotificationBroadCastReceiver;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Julius on 25.06.2015.
 */
public class DataBaseSingleton {
    private static final String BARCODE_NAME_FILE = "BARCODE_NAMES";
    private static DataBaseSingleton ourInstance;
    private HashMap<String, List<FridgeItem>> itemsByBarcode;
    private HashMap<Integer, FridgeItem> itemsById;
    private boolean loaded = false;
    private Context context;
    private final static String SETTINGS_FILE = "SETTINGS";
    private Settings settings;
    private HashMap<String, String> namesByBarcode;

    public static void init(Context context) {
        if (ourInstance != null) {
            return;
        }
        ourInstance = new DataBaseSingleton();
        ourInstance.context = context;
    }

    public static DataBaseSingleton getInstance() {
        return ourInstance;
    }

    private DataBaseSingleton() {
        this.itemsByBarcode = new HashMap<>();
        this.itemsById = new HashMap<>();
        this.namesByBarcode = new HashMap<>();
    }

    public String getNameByBarcode(String barcode){
        return namesByBarcode.get(barcode);
    }

    public FridgeItem getItemByID(int id) {
        FridgeItem item = itemsById.get(id);
        if (item == null) {
            item = itemsById.get(id);
        }
        return item;
    }

    public List<FridgeItem> getFridgeItems(String barCode) {
        return itemsByBarcode.get(barCode);
    }

    public void updateItem(FridgeItem from, FridgeItem to){
        if(to.getId() == -1){
            to.setId(new SharedPrefManager(context).getNewID());
        }

        if(to.getBarCode() != null){
            List<FridgeItem> list = itemsByBarcode.get(to.getBarCode());
            if(list != null){
                list.remove(to);
                if(list.size() > 0 && to.getQuantity() == 0){
                    deleteItem(to);
                    return; // delete current item because another item with the same barcode is present and the current one has no quantity left
                }
            }
            if (list == null) {
                list = new ArrayList<FridgeItem>();
                this.itemsByBarcode.put(to.getBarCode(), list);
            }
            if (!list.contains(to)) {
                list.add(to);
            }
            namesByBarcode.put(to.getBarCode(), to.getName());
        }

        if(from == null || from.getStore() != to.getStore()){ //if the storage point changed we need to remove the item from the one store and add it to the new store
            if(from != null){
                from.getStore().getItems().remove(to);
            }
            to.getStore().getItems().add(to);
        }

        if(!itemsById.containsKey(to.getId())){
            this.itemsById.put(to.getId(), to);
        }

        if (to.getNotificationDate() != null && to.getNotificationDate().getMillis() > SystemClock.elapsedRealtime()) {
            to.setNotified(false);
        }

        if(from != null){
            NotificationBroadCastReceiver.unregisterAlarm(context, from);
        }
        NotificationBroadCastReceiver.registerAlarm(context, to);


        if(from == null){
            HistoryHelper.newItem(to);
        }else{
            HistoryHelper.changeItem(from, to);
        }
    }

    public void deleteItem(FridgeItem item) {
        item.getStore().getItems().remove(item);

        if (item.getBarCode() != null) {
            List<FridgeItem> list = this.itemsByBarcode.get(item.getBarCode());
            if (list != null) {
                list.remove(item);
            }
        }
        this.itemsById.remove(item.getId());
        Iterable<FridgeItem> linkedItems = item.getLinkedItems();
        if(linkedItems != null){
            for(FridgeItem linkedItem : item.getLinkedItems()){
                List<FridgeItem> subLinkedItems = Collections.makeList(linkedItem.getLinkedItems());
                subLinkedItems.remove(item);
                linkedItem.setLinkedItems(subLinkedItems);
            }
        }
        NotificationBroadCastReceiver.unregisterAlarm(context, item);
        HistoryHelper.deleteItem(item);
    }

    public void saveDataBase() {
        Gson gson = ExtendedGson.getInstance();
        String json = gson.toJson(settings);
        try {
            FileAccess.writeToStorage(json, SETTINGS_FILE);
            for (Store store : settings.getStores()) {
                ArrayList<FridgeItem> toSave = new ArrayList<>(store.getItems());
                json = gson.toJson(toSave);
                FileAccess.writeToStorage(json, store.getName());
            }
        } catch (StorageException ex) {
            showStorageError();
        }
        json = gson.toJson(namesByBarcode);
        try {
            FileAccess.writeToStorage(json, BARCODE_NAME_FILE);
        } catch (StorageException ex) {
            showStorageError();
        }
    }


    public void loadDataBase() {
        if (loaded)
            return;

        Gson gson = ExtendedGson.getInstance();

        this.itemsByBarcode.clear();
        this.itemsById.clear();
        int biggestID = -1;

        try {
            String json = FileAccess.readFromStorage(BARCODE_NAME_FILE);
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            namesByBarcode = gson.fromJson(json, type);

            if(namesByBarcode == null){
                namesByBarcode = new HashMap<>();
            }
        } catch (StorageException ex) {
            showStorageError();
        }


        try {
            String json = FileAccess.readFromStorage(SETTINGS_FILE);
            if (json != null) {
                settings = gson.fromJson(json, Settings.class);
                for (Store store : settings.getStores()) {
                    json = FileAccess.readFromStorage(store.getName());
                    if (json != null) {
                        Type listType = new TypeToken<ArrayList<FridgeItem>>() {
                        }.getType();
                        ArrayList<FridgeItem> items = gson.fromJson(json, listType);
                        for (FridgeItem item : items) {
                            item.setStore(store);
                            if (item.getBarCode() != null) {
                                List<FridgeItem> list = this.itemsByBarcode.get(item.getBarCode());
                                if (list == null) {
                                    list = new ArrayList<>();
                                    this.itemsByBarcode.put(item.getBarCode(), list);
                                }
                                list.add(item);
                                if (!namesByBarcode.containsKey(item.getBarCode())) {
                                    namesByBarcode.put(item.getBarCode(), item.getName());
                                }
                            }
                            this.itemsById.put(item.getId(), item);
                            if (item.getId() > biggestID) {
                                biggestID = item.getId();
                            }
                        }
                        store.setItems(items);
                    }
                }
            } else {
                settings = new Settings();
                settings.setStores(new ArrayList<Store>());
            }
        } catch (StorageException ex) {
            showStorageError();
        }
        SharedPrefManager manager = new SharedPrefManager(context);
        manager.saveNewID(biggestID);

        //set linked Items in the FridgeItem
        for(FridgeItem item : itemsById.values()){
            if(item.getLinkedItemIds() != null) {
                List<FridgeItem> linkedItems = new ArrayList<FridgeItem>();
                for (Integer id : item.getLinkedItemIds()) {
                    FridgeItem referencedItem = itemsById.get(id);
                    if(referencedItem != null){
                        linkedItems.add(referencedItem);
                    }
                }
                item.setLinkedItems(linkedItems);
            }
        }

        loaded = true;
    }

    public void showStorageError() {
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

    public List<Store> getStores() {
        return settings.getStores();
    }

    public void saveStore(Store store) {
        settings.getStores().add(store);
    }
    public void updateStore(Store oldStore, Store newStore){
        if(!oldStore.getName().equals(newStore.getName())){
            FileAccess.renameFile(oldStore.getName(), newStore.getName());
        }
    }

    public void deleteStore(Store store) {
        settings.getStores().remove(store);
    }

    public void linkItems(List<FridgeItem> itemsToLink) {
        List<FridgeItem> allLinks = new ArrayList<>(itemsToLink);
        for(FridgeItem item : itemsToLink){
            Iterable<FridgeItem> alreadyLinkedItems = item.getLinkedItems();
            if(alreadyLinkedItems != null) {
                for (FridgeItem alreadyLinked : alreadyLinkedItems) {
                    if (!allLinks.contains(alreadyLinked)) {
                        allLinks.add(alreadyLinked);
                    }
                }
            }
        }
        for(FridgeItem item : allLinks){
            List<FridgeItem> linklist = new ArrayList<>(allLinks);
            linklist.remove(item);
            item.setLinkedItems(linklist);
        }
        saveDataBase();
        HistoryHelper.linkedItems(allLinks);
    }
    public void removeLinks(FridgeItem item) {
        Iterable<FridgeItem> linkedItems = item.getLinkedItems();
        if(linkedItems == null){
            return;
        }else{
            for(FridgeItem linkedItem : linkedItems){
                Iterable<FridgeItem> subLinkedItems = linkedItem.getLinkedItems();
                if(subLinkedItems != null){
                    List<FridgeItem> linklist = Collections.makeList(subLinkedItems);
                    linklist.remove(item);
                    linkedItem.setLinkedItems(linklist);
                }
            }
        }
        item.setLinkedItems(null);
        saveDataBase();
        HistoryHelper.removedLinks(item, linkedItems);
    }
}
