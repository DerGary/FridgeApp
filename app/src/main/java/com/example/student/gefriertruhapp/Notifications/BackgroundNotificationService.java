package com.example.student.gefriertruhapp.Notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Stefan on 02-07-15.
 */
public class BackgroundNotificationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        NotificationBroadCastReceiver.registerAlarm(getBaseContext());

        return Service.START_NOT_STICKY;
    }
}
