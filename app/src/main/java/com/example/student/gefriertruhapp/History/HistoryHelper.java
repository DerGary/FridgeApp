package com.example.student.gefriertruhapp.History;

import android.content.Context;

import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Serialization.FileAccess;
import com.example.student.gefriertruhapp.Model.FridgeItem;

import java.util.List;

/**
 * Created by Stefan on 11-10-16.
 */
public class HistoryHelper {
    public static void newItem(Context context, FridgeItem item) {
        FileAccess.writeHistory(context.getString(R.string.new_entry_created) +
                context.getString(R.string.name) + ": " + item.getName() + " \r\n" +
                context.getString(R.string.stock) + ": "+item.getStore().getName()+"\r\n" +
                context.getString(R.string.quantity) + ": " + item.getQuantity() + "\r\n" +
                context.getString(R.string.min_quantity) + ": " + item.getMinQuantity()+ "\r\n" +
                context.getString(R.string.note) + ": " + item.getNotesOrPlaceholderIfEmpty(context) + "\r\n" +
                context.getString(R.string.reminder_date) + ": " + item.getNotificationDateString(context)+ "\r\n");
    }

    public static void deleteItem(Context context, FridgeItem item) {
        FileAccess.writeHistory(context.getString(R.string.entry)+" "+item.getName()+" " +context.getString(R.string.deleted)+". \r\n" +
                context.getString(R.string.stock)+": "+item.getStore().getName()+"\r\n" +
                context.getString(R.string.quantity)+": " + item.getQuantity() + "\r\n");
    }

    public static void changeItem(Context context, FridgeItem oldItem, FridgeItem newItem) {
        String text = context.getString(R.string.entry) +" "+oldItem.getName()+" "+context.getString(R.string.changed)+":\r\n";
        if(!oldItem.getName().equals(newItem.getName())){
            text += context.getString(R.string.name)+": " + oldItem.getName() + " -> " + newItem.getName() + "\r\n";
        }
        if (oldItem.getStore() != newItem.getStore()) {
            text += context.getString(R.string.stock)+": " + oldItem.getStore().getName() + " -> " + newItem.getStore().getName() + "\r\n";
        }
        if (oldItem.getQuantity() != newItem.getQuantity()) {
            text += context.getString(R.string.quantity)+": " + oldItem.getQuantity() + " -> " + newItem.getQuantity() + "\r\n";
        }
        if (oldItem.getMinQuantity() != newItem.getMinQuantity()) {
            text += context.getString(R.string.min_quantity)+": " + oldItem.getMinQuantity() + " -> " + newItem.getMinQuantity() + "\r\n";
        }
        if (oldItem.getNotificationDate() != newItem.getNotificationDate()) {
            text += context.getString(R.string.reminder_date)+": " + oldItem.getNotificationDateString(context) + " -> " + newItem.getNotificationDateString(context) + "\r\n";
        }
        if (!oldItem.getNotes().equals(newItem.getNotes())) {
            text += context.getString(R.string.note)+": " + oldItem.getNotesOrPlaceholderIfEmpty(context) + " -> " + newItem.getNotesOrPlaceholderIfEmpty(context) + "\r\n";
        }

        FileAccess.writeHistory(text);
    }

    public static void linkedItems(Context context, Iterable<FridgeItem> linkedList){
        String text = context.getString(R.string.following_items_linked)+" :\r\n";
        for(FridgeItem item : linkedList){
            text+=item.getName() + "\r\n";
        }
        FileAccess.writeHistory(text);
    }
    public static void removedLinks(Context context, FridgeItem item, Iterable<FridgeItem> linkedItems){
        String text = context.getString(R.string.link_of_entry)+" "+item.getName()+" "+context.getString(R.string.removed) +
                ". \r\n" +
                context.getString(R.string.following_links_removed) +
                "\r\n";
        if(linkedItems != null) {
            for (FridgeItem linkedItem : linkedItems) {
                text += linkedItem.getName() + "\r\n";
            }
        }
        FileAccess.writeHistory(text);
    }
    public static void doInventory(Context context, Store store){
        StringBuilder builder = new StringBuilder(context.getString(R.string.inventory_of_stock)).append(store.getName()).append(" ").append(context.getString(R.string.executed)).append(".\r\n");
        builder.append(context.getString(R.string.following_changes_done));
        builder.append(" \r\n");
        for(FridgeItem item : store.getItems()){
            if(item.getQuantity() != item.getGotQuantity()){
                builder.append(context.getString(R.string.entry)).append(": ").append(item.getName()).append("\r\n");
                builder.append(context.getString(R.string.quantity)).append(": ").append(item.getQuantity()).append(" -> ").append(item.getGotQuantity()).append("\r\n");
            }
        }
        FileAccess.writeHistory(builder.toString());
    }
}
