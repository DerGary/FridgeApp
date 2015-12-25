package com.example.student.gefriertruhapp.Notifications;

/**
 * Created by student on 24.12.15.
 */

import android.content.BroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctxt, Intent i) {
        NotificationBroadCastReceiver.registerAllAlarms(ctxt);
        Notifier.sendNotificationsNotSendYet(ctxt);
    }
}