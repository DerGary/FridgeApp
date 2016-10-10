package com.example.student.gefriertruhapp.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Settings.Store;

import org.joda.time.DateTime;

/**
 * Created by Stefan on 02-07-15.
 */
public class NotificationBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(Notifier.ITEM_ID, -1);


        if(id == -1){
            return;
        }


        Notifier.sendNotification(context, id);
    }






    public static void registerAllAlarms(Context context) {
        DataBaseSingleton.init(context);
        DataBaseSingleton.getInstance().loadDataBase();
        for(Store store : DataBaseSingleton.getInstance().getStores()) {
            for(FridgeItem item : store.getItems()){
                registerAlarm(context, item);
            }
        }
    }

    public static void unregisterAlarm(Context context, FridgeItem item){
        Intent i = new Intent(context, NotificationBroadCastReceiver.class);
        i.putExtra(Notifier.ITEM_ID, item.getId());
        PendingIntent sender = PendingIntent.getBroadcast(context, item.getId(), i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    public static void registerAlarm(Context context, FridgeItem item){
        if(item.isNotified()){
            return;
        }
        if(item.getNotificationDate() == null) {
            return;
        }
        if(item.getNotificationDate().getMillis() < DateTime.now().getMillis()){
            return;
        }

        Intent i = new Intent(context, NotificationBroadCastReceiver.class);
        i.putExtra(Notifier.ITEM_ID, item.getId());
        PendingIntent sender = PendingIntent.getBroadcast(context, item.getId(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, item.getNotificationDate().getMillis(), sender);
    }
}
