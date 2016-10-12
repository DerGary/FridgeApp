package com.example.student.gefriertruhapp.Helper;

import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Settings.Store;
import com.opencsv.CSVParser;
import com.opencsv.CSVWriter;

import org.joda.time.DateTime;

import java.io.FileWriter;
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

    public static void writeStore(Store store){
        List<String[]> text = new ArrayList<>();
        String[] header = new String[] { "ID", "Name", "Anzahl", "MinAnzahl", "Erinnerungsdatum", "Barcode", "Notiz", "Bereits erinnert" };
        text.add(header);
        for(FridgeItem item : store.getItems()){
            String[] column = new String[] { String.valueOf(item.getId()), item.getName(), String.valueOf(item.getQuantity()), String.valueOf(item.getMinQuantity()), item.getNotificationDateString(), item.getBarCode(), item.getNotes(), String.valueOf(item.isNotified())};
            text.add(column);
        }
        FileAccess.writeCSV(text, store.getName());
    }
}
