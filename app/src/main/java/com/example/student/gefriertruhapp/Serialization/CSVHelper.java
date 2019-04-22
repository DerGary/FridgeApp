package com.example.student.gefriertruhapp.Serialization;

import android.app.Application;
import android.content.Context;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 12-10-16.
 */
public class CSVHelper {

    private int id;
    private String name;
    private int quantity;
    private DateTime notificationDate;
    private String barCode;
    private String notes;
    private boolean notified = false;
    private int minQuantity;
    private transient Store store;

    public static void writeStore(Context context, Store store){
        List<String[]> text = new ArrayList<>();
        String[] header = new String[] { "ID", context.getString(R.string.name), context.getString(R.string.quantity), context.getString(R.string.min_quantity), context.getString(R.string.reminder_date), context.getString(R.string.barcode), context.getString(R.string.note), context.getString(R.string.already_reminded) };
        text.add(header);
        for(FridgeItem item : store.getItems()){
            String[] column = new String[] { String.valueOf(item.getId()), item.getName(), String.valueOf(item.getQuantity()), String.valueOf(item.getMinQuantity()), item.getNotificationDateString(context), item.getBarCode(), item.getNotes(), String.valueOf(item.isNotified())};
            text.add(column);
        }
        FileAccess.writeCSV(text, store.getName());
    }


}
