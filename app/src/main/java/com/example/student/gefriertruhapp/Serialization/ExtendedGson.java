package com.example.student.gefriertruhapp.Serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

/**
 * Created by Stefan on 14-07-15.
 */
public abstract class ExtendedGson {
    public static Gson getInstance() {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer()).create();
    }
}
