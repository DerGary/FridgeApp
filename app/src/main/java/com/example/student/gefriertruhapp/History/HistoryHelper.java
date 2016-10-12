package com.example.student.gefriertruhapp.History;

import com.example.student.gefriertruhapp.Serialization.FileAccess;
import com.example.student.gefriertruhapp.Model.FridgeItem;

/**
 * Created by Stefan on 11-10-16.
 */
public class HistoryHelper {
    public static void newItem(FridgeItem item) {
        FileAccess.writeHistory("Neuer Eintrag erzeugt. \r\n" +
                "Name: " + item.getName() + " \r\n" +
                "Lager: "+item.getStore().getName()+"\r\n" +
                "Anzahl: " + item.getQuantity() + "\r\n" +
                "MinAnzahl: " + item.getMinQuantity()+ "\r\n" +
                "Notiz: " + item.getNotesOrPlaceholderIfEmpty() + "\r\n" +
                "Erinnerungsdatum: " + item.getNotificationDateString()+ "\r\n");
    }

    public static void deleteItem(FridgeItem item) {
        FileAccess.writeHistory("Eintrag "+item.getName()+" gelöscht. \r\n" +
                "Lager: "+item.getStore().getName()+"\r\n" +
                "Anzahl: " + item.getQuantity() + "\r\n");
    }

    public static void changeItem(FridgeItem oldItem, FridgeItem newItem) {
        String text = "Eintrag "+oldItem.getName()+" geändert:\r\n";
        if(!oldItem.getName().equals(newItem.getName())){
            text += "Name: " + oldItem.getName() + " -> " + newItem.getName() + "\r\n";
        }
        if (oldItem.getStore() != newItem.getStore()) {
            text += "Lager: " + oldItem.getStore().getName() + " -> " + newItem.getStore().getName() + "\r\n";
        }
        if (oldItem.getQuantity() != newItem.getQuantity()) {
            text += "Anzahl: " + oldItem.getQuantity() + " -> " + newItem.getQuantity() + "\r\n";
        }
        if (oldItem.getMinQuantity() != newItem.getMinQuantity()) {
            text += "MinAnzahl: " + oldItem.getMinQuantity() + " -> " + newItem.getMinQuantity() + "\r\n";
        }
        if (oldItem.getNotificationDate() != newItem.getNotificationDate()) {
            text += "Erinnerungsdatum: " + oldItem.getNotificationDateString() + " -> " + newItem.getNotificationDateString() + "\r\n";
        }
        if (!oldItem.getNotes().equals(newItem.getNotes())) {
            text += "Notizen: " + oldItem.getNotesOrPlaceholderIfEmpty() + " -> " + newItem.getNotesOrPlaceholderIfEmpty() + "\r\n";
        }

        FileAccess.writeHistory(text);
    }
}
