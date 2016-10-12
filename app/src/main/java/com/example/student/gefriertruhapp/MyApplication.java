package com.example.student.gefriertruhapp;

import android.app.Application;
import android.util.Log;

import com.example.student.gefriertruhapp.Serialization.FileAccess;

/**
 * Created by student on 31.12.15.
 */
public class MyApplication extends Application {
    // uncaught exception handler variable
    private Thread.UncaughtExceptionHandler defaultUEH;

    public void onCreate ()
    {
        super.onCreate();
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });
    }

    public void handleUncaughtException(Thread thread, Throwable e)
    {
        Log.e(null, e.toString());
        ///e.printStackTrace(); // not all Android versions will print the stack trace automatically

        FileAccess.writeLog();

        defaultUEH.uncaughtException(thread, e);
    }
}
