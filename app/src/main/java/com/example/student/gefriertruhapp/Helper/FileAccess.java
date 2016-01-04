package com.example.student.gefriertruhapp.Helper;

import android.app.AlertDialog;
import android.app.Application;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by student on 31.12.15.
 */
public abstract class FileAccess {
    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean writeToExternalStorage(String json, String fileName){
        if(!isExternalStorageMounted())
            return false;

        File dir = Environment.getExternalStorageDirectory();
        File appDir = new File(dir, "Gefriertruhen App");
        appDir.mkdirs();
        File appFile = new File(appDir, fileName + ".json");



        try {
            if(!appFile.exists()){
                appFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(appFile);
            IOUtils.write(json, fos);
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    public static String readFromExternalStorage(String fileName){
        if(!isExternalStorageMounted())
            return null;

        File dir = Environment.getExternalStorageDirectory();
        File appDir = new File(dir, "Gefriertruhen App");
        File appFile = new File(appDir, fileName + ".json");

        try {
            FileInputStream fis = new FileInputStream(appFile);
            return IOUtils.toString(fis);
        } catch (Exception ex){
            return null;
        }
    }

    public static boolean writeToInternalStorage(String json, String fileName){
        File dir = Environment.getDataDirectory();
        File appDir = new File(dir, "Gefriertruhen App");
        appDir.mkdirs();
        File appFile = new File(appDir, fileName + ".json");


        try {
            if(!appFile.exists()){
                appFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(appFile);
            IOUtils.write(json, fos);
            return true;
        } catch (Exception ex){
            return false;
        }
    }
    public static String readFromInternalStorage(String fileName){
        File dir = Environment.getDataDirectory();
        File appDir = new File(dir, "Gefriertruhen App");
        File appFile = new File(appDir, fileName + ".json");

        try {
            FileInputStream fis = new FileInputStream(appFile);
            return IOUtils.toString(fis);
        } catch (Exception ex){
            return null;
        }
    }

    public static void writeToStorage(String json, String fileName) throws StorageException {
        if(!isExternalStorageMounted()){
            throw new StorageException();
        }

        writeToExternalStorage(json, fileName);
    }

    public static String readFromStorage(String fileName) throws StorageException {
        if(!isExternalStorageMounted()){
            throw new StorageException();
        }
        return readFromExternalStorage(fileName);
    }

    public static void writeLog(){
        if(!isExternalStorageMounted())
            return;

        File dir = Environment.getExternalStorageDirectory();
        File appDir = new File(dir, "Gefriertruhen App/Log");
        appDir.mkdirs();

        String date = DateTime.now().toString(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"));



        String[] cmd = new String[] {"logcat","-f",appDir.getAbsolutePath() + "/" + date+" log.txt","-v","time","*:E"};
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }
}
