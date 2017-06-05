package com.example.student.gefriertruhapp.UPC;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.student.gefriertruhapp.Serialization.ExtendedGson;
import com.example.student.gefriertruhapp.R;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAsyncTask<T> extends AsyncTask<String, Void, T> {
    final static String TAG = "GetAsyncTask";
    private Activity _context;
    private String _upcKey;

    public GetAsyncTask(Activity context, String upcKey) {
        this._context = context;
        this._upcKey = upcKey;
    }

    @Override
    protected T doInBackground(String... barCode) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://api.upcdatabase.org/json/"+ _upcKey +"/"+ barCode[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);

            Gson gson = ExtendedGson.getInstance();

            Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            String json = IOUtils.toString(urlConnection.getInputStream());
            T object = gson.fromJson(json, tClass);

            return object;
        } catch (Exception ex) {
            Log.e(TAG, "" + ex.getMessage());
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
