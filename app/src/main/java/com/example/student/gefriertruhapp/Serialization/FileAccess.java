package com.example.student.gefriertruhapp.Serialization;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.opencsv.CSVWriter;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by student on 31.12.15.
 */
public abstract class FileAccess {
    private static final String EXTENSION_TXT = "txt";
    private static final String EXTENSION_JSON = "json";
    private static final String EXTENSION_CSV = "csv";
    private static final String APP_FOLDER = "Gefriertruhen App";
    private static final String HISTORY_FOLDER = "History";
    private static final String LOG_FOLDER = "Log";
    private static final String EXPORT_FOLDER = "Export";
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }

        return true;
    }

    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean writeToExternalStorage(String text, String fileName, String folder, String extension) {
        if (!isExternalStorageMounted())
            return false;

        File dir = Environment.getExternalStorageDirectory();
        File appDir = null;
        File appFile = null;
        if (folder != null) {
            appDir = new File(dir, APP_FOLDER + "/" + folder);
        } else {
            appDir = new File(dir, APP_FOLDER);
        }
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        if (extension == null) {
            extension = EXTENSION_JSON;
        }
        appFile = new File(appDir, fileName + "." + extension);


        try {
            if (!appFile.exists()) {
                appFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(appFile);
            IOUtils.write(text, fos);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static String readFromExternalStorage(String fileName, String folder, String extension) {
        if (!isExternalStorageMounted())
            return null;

        File dir = Environment.getExternalStorageDirectory();

        File appDir = null;
        File appFile = null;
        if (folder != null) {
            appDir = new File(dir, APP_FOLDER + "/" + folder);
        } else {
            appDir = new File(dir, APP_FOLDER);
        }
        if (extension == null) {
            extension = EXTENSION_JSON;
        }
        appFile = new File(appDir, fileName + "." + extension);

        try {
            FileInputStream fis = new FileInputStream(appFile);
            return IOUtils.toString(fis);
        } catch (Exception ex) {
            return null;
        }
    }

//    public static boolean writeToInternalStorage(String json, String fileName) {
//        File dir = Environment.getDataDirectory();
//        File appDir = new File(dir, APP_FOLDER);
//        appDir.mkdirs();
//        File appFile = new File(appDir, fileName + "." + EXTENSION_JSON);
//
//
//        try {
//            if (!appFile.exists()) {
//                appFile.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(appFile);
//            IOUtils.write(json, fos);
//            return true;
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//    public static String readFromInternalStorage(String fileName) {
//        File dir = Environment.getDataDirectory();
//        File appDir = new File(dir, APP_FOLDER);
//        File appFile = new File(appDir, fileName + "." + EXTENSION_JSON);
//
//        try {
//            FileInputStream fis = new FileInputStream(appFile);
//            return IOUtils.toString(fis);
//        } catch (Exception ex) {
//            return null;
//        }
//    }

    public static void writeToStorage(String json, String fileName) throws StorageException {
        if (!isExternalStorageMounted()) {
            throw new StorageException();
        }

        writeToExternalStorage(json, fileName, null, null);
    }

    public static String readFromStorage(String fileName) throws StorageException {
        if (!isExternalStorageMounted()) {
            throw new StorageException();
        }
        return readFromExternalStorage(fileName, null, null);
    }

    public static void writeLog() {
        if (!isExternalStorageMounted())
            return;

        File dir = Environment.getExternalStorageDirectory();
        File appDir = new File(dir, APP_FOLDER + "/" + LOG_FOLDER);
        appDir.mkdirs();

        String date = DateTime.now().toString(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"));

        String[] cmd = new String[]{"logcat", "-f", appDir.getAbsolutePath() + "/" + date + " log.txt", "-v", "time", "*:E"};
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public static void writeHistory(String text) {
        if (!isExternalStorageMounted()) {
            return;
        }

        String date = DateTime.now().toString(DateTimeFormat.forPattern("YYYY-MM-dd"));
        String time = DateTime.now().toString(DateTimeFormat.forPattern("HH:mm:ss:"));
        String prevText = readFromExternalStorage(date, HISTORY_FOLDER, EXTENSION_TXT);
        text = time + "\r\n" + text;
        if (prevText != null) {
            text = text + "\r\n" + prevText;
        }
        writeToExternalStorage(text, date, HISTORY_FOLDER, EXTENSION_TXT);
    }

    public static Map<String, String> readHistory(int dayCount) {
        File dir = Environment.getExternalStorageDirectory();
        File appDir = null;
        appDir = new File(dir, APP_FOLDER + "/" + HISTORY_FOLDER);
        File[] fileList = appDir.listFiles();
        if(fileList == null){
            return null;
        }
        Arrays.sort(fileList, Collections.<File>reverseOrder());
        if (fileList.length < dayCount) {
            dayCount = fileList.length;
        }
        try {
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < dayCount; i++) {
                File file = fileList[i];
                FileInputStream fis = new FileInputStream(file);
                String text = IOUtils.toString(fis);
                map.put(file.getName().replace("." + EXTENSION_TXT, ""), text);
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void writeCSV(List<String[]> lines, String fileName) {
        File dir = Environment.getExternalStorageDirectory();
        File appDir = null;
        appDir = new File(dir, APP_FOLDER + "/" + EXPORT_FOLDER);
        appDir.mkdir();
        String date = DateTime.now().toString(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"));
        File exportFile = new File(appDir, date + " " + fileName + "." + EXTENSION_CSV);

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(exportFile.getAbsolutePath()), ',', '"', '"', "\r\n");
            writer.writeAll(lines);
            writer.close();
        } catch (Exception ex) {

        }
    }

    public static void renameFile(String oldName, String newName){
        File dir = Environment.getExternalStorageDirectory();
        File appDir = new File(dir, APP_FOLDER);
        File appFile = new File(appDir, oldName + "." + EXTENSION_JSON);
        File newFile = new File(appDir, newName + "." + EXTENSION_JSON);
        appFile.renameTo(newFile);
    }

}
