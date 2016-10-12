package com.example.student.gefriertruhapp.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Model.Store;

/**
 * Created by student on 25.12.15.
 */
public abstract class Notifier {

    public final static String ITEM_ID = "ITEM_ID";
    public static void sendNotification(Context context, int id){
        DataBaseSingleton.init(context);
        DataBaseSingleton.getInstance().loadDataBase();


        FridgeItem item = DataBaseSingleton.getInstance().getItemByID(id);
        item.setNotified(true);
        DataBaseSingleton.getInstance().saveDataBase();

        Intent startIntent = new Intent(context, Dashboard.class);
        startIntent.putExtra(ITEM_ID, item.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, item.getId(), startIntent, PendingIntent.FLAG_ONE_SHOT);

        String shelf = item.getStore().getName() + ": ";

        long[] vibrate = new long[] {1000,500,1000,500,1000,500};
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_freezer_blue);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icon_freezer_blue)
                .setLargeIcon(bm)
                .setContentTitle(item.getName())
                .setContentText(shelf + "Erinnerung an: " + item.getName())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLights(Color.argb(255,0, 150, 136), 1000, 1000);
        Notification n;
        if (Build.VERSION.SDK_INT < 16) {
            n = mBuilder.getNotification();
        } else {
            n = mBuilder.build();
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(item.getId(), n);
    }

    public static void sendNotificationsNotSendYet(Context context){
        DataBaseSingleton.init(context);
        DataBaseSingleton.getInstance().loadDataBase();
        for(Store store : DataBaseSingleton.getInstance().getStores()) {
            for(FridgeItem item : store.getItems()){
                if(item.getNotificationDate() != null && !item.isNotified() && item.getNotificationDate().getMillis() < SystemClock.elapsedRealtime()){
                    sendNotification(context, item.getId());
                }
            }
        }
    }
}
