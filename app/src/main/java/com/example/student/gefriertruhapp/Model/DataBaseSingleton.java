package com.example.student.gefriertruhapp.Model;

import android.accounts.AccountAuthenticatorActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;

import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.History.HistoryHelper;
import com.example.student.gefriertruhapp.R;
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
    private static final String CATEGORY_FILE = "CATEGORIES";
    private final static String SETTINGS_FILE = "SETTINGS";

    private static DataBaseSingleton ourInstance;

    private HashMap<String, List<FridgeItem>> itemsByBarcode;
    private HashMap<Integer, FridgeItem> itemsById;
    private HashMap<String, String> namesByBarcode;
    private HashMap<Integer, Category> categoryById;
    private HashMap<Category, List<FridgeItem>> itemsByCategory;

    private boolean loaded = false;
    private Context context;
    private Settings settings;

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
        this.categoryById = new HashMap<>();
        this.itemsByCategory = new HashMap<>();
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

    public List<Category> getCategories(){ return Collections.makeList(categoryById.values()); }

    public List<FridgeItem> getFridgeItems(String barCode) {
        return itemsByBarcode.get(barCode);
    }
    public List<FridgeItem> getFridgeItems(Category category) {
        return itemsByCategory.get(category);
    }

    public void updateItem(FridgeItem from, FridgeItem to){
        if(to.getId() == -1){
            to.setId(new SharedPrefManager(context).getNewID());
        }

        if(to.getQuantity() == 0 && to.getLinkedItems() != null && !Collections.isEmpty(to.getLinkedItems())){
            // when quantity is zero and we have a link to another item we can delete this item because we have another item that is equivalent to this item.
            deleteItem(to);
            return;
        }

        if(to.getBarCode() != null){
            List<FridgeItem> list = itemsByBarcode.get(to.getBarCode());
            if(list != null){
                list.remove(to);
                if(list.size() > 0 && to.getQuantity() == 0){
                    deleteItem(to);
                    return; // delete current item because another item with the same barcode is present and the current one has no quantity left
                }else if(list.size() > 0){
                    //when we add an item that has the same barcode than other items in the database we need to delete those items if they have a quantity of zero
                    List<FridgeItem> toDelete = new ArrayList<>();
                    for(FridgeItem item : list){
                        if(item.getQuantity() == 0){
                            toDelete.add(item);
                        }
                    }
                    for(FridgeItem item : toDelete){
                        deleteItem(item);
                    }
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

        if(to.getCategory() != null){
            List<FridgeItem> list = null;
            if(itemsByCategory.containsKey(to.getCategory())){
                list = itemsByCategory.get(to.getCategory());
            }else{
                list = new ArrayList<>();
                itemsByCategory.put(to.getCategory(), list);
            }
            if(!list.contains(to)){
                list.add(to);
            }
        }

        if(from == null){
            HistoryHelper.newItem(context, to);
        }else{
            if(from.getCategory() != null){
                if(itemsByCategory.containsKey(from.getCategory())){
                    List<FridgeItem> items = itemsByCategory.get(from.getCategory());
                    items.remove(to);
                    if(items.isEmpty()){
                        itemsByCategory.remove(from.getCategory());
                        categoryById.remove(from.getCategory().getId());
                    }
                }
            }

            HistoryHelper.changeItem(context, from, to);
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

        if(item.getCategory() != null){
            List<FridgeItem> list = itemsByCategory.get(item.getCategory());
            list.remove(item);
            if(list.isEmpty()){
                itemsByCategory.remove(item.getCategory());
                categoryById.remove(item.getCategory().getId());
            }
        }

        NotificationBroadCastReceiver.unregisterAlarm(context, item);
        HistoryHelper.deleteItem(context, item);
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

        json = gson.toJson(categoryById.values());
        try {
            FileAccess.writeToStorage(json, CATEGORY_FILE);
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
        this.categoryById.clear();
        int biggestID = -1;

        try{
            String json = FileAccess.readFromStorage(CATEGORY_FILE);
            Type type = new TypeToken<ArrayList<Category>>() {
            }.getType();
            List<Category> categories = gson.fromJson(json, type);
            if(categories != null){
                for(Category cat : categories){
                    categoryById.put(cat.getId(), cat);
                    if(cat.getId() > biggestID){
                        biggestID = cat.getId();
                    }
                    itemsByCategory.put(cat, new ArrayList<FridgeItem>());
                }
            }
        }catch (StorageException ex){
            showStorageError();
        }

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
                            if(item.getCategoryId() != 0){
                                Category cat = categoryById.get(item.getCategoryId());
                                item.setCategory(cat);
                                List<FridgeItem> itemsByCat = null;
                                if(itemsByCategory.containsKey(cat)){
                                    itemsByCat = itemsByCategory.get(cat);
                                }else{
                                    itemsByCat = new ArrayList<>();
                                }
                                itemsByCat.add(item);
                                itemsByCategory.put(cat, itemsByCat);
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
        builder.setTitle(R.string.no_storage);
        builder.setMessage(R.string.no_storage_message);
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
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

        // when linking two or more items where one or more items have a quantity of 0 we delete the items that are zero.
        // if all items have zero quantity we need to preserve one of the items.
        List<FridgeItem> toDelete = new ArrayList<FridgeItem>();
        for(FridgeItem item : allLinks){
            if(item.getQuantity() == 0){
                toDelete.add(item);
            }
        }
        if(toDelete.size() == allLinks.size() && toDelete.size() > 0){
            toDelete.remove(0);
        }
        for(FridgeItem item : toDelete){
            deleteItem(item);
        }

        // save changes
        saveDataBase();
        HistoryHelper.linkedItems(context, allLinks);
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
        HistoryHelper.removedLinks(context, item, linkedItems);
    }

    public Category createNewCategory(String name){
        Category cat = new Category();
        cat.setName(name);
        cat.setId(new SharedPrefManager(context).getNewID());
        categoryById.put(cat.getId(), cat);
        return cat;
    }
}
