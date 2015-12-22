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

    private final Context _context;
    private SharedPreferences _pref;
    private SharedPreferences.Editor _prefEditor;

    public SharedPrefManager(Context context) {
        this._context = context;
        _pref = context.getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE);
        _prefEditor = _pref.edit();
    }

    public <T> boolean save(String item, T obj){
        Gson gson = ExtendedGson.getInstance();
        String json = gson.toJson(obj);

        _prefEditor.putString(item, json);
        return _prefEditor.commit();
    }

    public <T> T load(String item, Type type) {
        String json = _pref.getString(item, "");
        Gson gson = ExtendedGson.getInstance();

        if (!TextUtils.isEmpty(json)) {
            return gson.fromJson(json, type);
        }
        return null;
    }

    public void delete() {
        _prefEditor.clear();
        _prefEditor.commit();
    }

}
