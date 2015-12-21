package com.example.student.gefriertruhapp.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.student.gefriertruhapp.Helper.ExtendedGson;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 09-04-15.
 */
public class SharedPrefManager {
    private final static String LECTURE_TAG = "lecture";
    private final static String My_PREFS_NAME = "SHARED_USER_PREFERENCES";
    private final static String FRIDGE_ITEMS= "FRIDGE_ITEMS";
    private final static String SHELF_ITEMS = "SHELF_ITEMS";
    private final Context _context;
    private SharedPreferences _pref;
    private SharedPreferences.Editor _prefEditor;

    public SharedPrefManager(Context context) {
        this._context = context;
        _pref = context.getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE);
        _prefEditor = _pref.edit();
    }

    public boolean saveDataBase() {
        List<FridgeItem> fridgeItems = DataBaseSingleton.getInstance().get_fridgeList();
        List<ShelfItem> shelfItems = DataBaseSingleton.getInstance().get_shelfList();

        Gson gson = ExtendedGson.getInstance();
        String fridgeJson = gson.toJson(fridgeItems);
        String shelfJson = gson.toJson(shelfItems);

        _prefEditor.putString(FRIDGE_ITEMS, fridgeJson);
        _prefEditor.putString(SHELF_ITEMS, shelfJson);
        return _prefEditor.commit();
    }

    public void loadDataBase() {
        String fridgeJson = _pref.getString(FRIDGE_ITEMS, "").toString();
        String shelfJson = _pref.getString(SHELF_ITEMS, "").toString();
        Gson gson = ExtendedGson.getInstance();

        if (!TextUtils.isEmpty(fridgeJson)) {
            Type listType = new TypeToken<ArrayList<FridgeItem>>() {}.getType();
            ArrayList<FridgeItem> fridgeItems = gson.fromJson(fridgeJson, listType);
            if (fridgeItems != null) {
                DataBaseSingleton.getInstance().set_fridgeList(fridgeItems);
            }
        }

        if (!TextUtils.isEmpty(shelfJson)) {
            Type listType = new TypeToken<ArrayList<ShelfItem>>() {}.getType();

            ArrayList<ShelfItem> shelfItems = gson.fromJson(shelfJson, listType);
            if (shelfItems != null) {
                DataBaseSingleton.getInstance().set_shelfList(shelfItems);
            }
        }
    }

    public void delete() {
        _prefEditor.clear();
        _prefEditor.commit();
    }

    public void saveNote(int lectureID, String note) {
        _prefEditor.putString(LECTURE_TAG + lectureID, note);
        _prefEditor.commit();
    }

    public String getNote(int lectureID) {
        return _pref.getString(LECTURE_TAG + lectureID, "");
    }
}
