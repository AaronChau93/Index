package com.aaron.chau.index.models;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Aaron Chau on 2/27/2016.
 */
public class MySqlViaPHP extends AsyncTask<String, Void, JSONArray> {
    private static final boolean DEBUG = true;
    private static final String TAG = MySqlViaPHP.class.getName();
    private static final String LINK =
            "https://students.washington.edu/chau93/index_app/query.php?sql=";


    public MySqlViaPHP() {

    }

    @Override
    protected JSONArray doInBackground(String... theSQL) {
        JSONArray jArr = null;
        try {
            String sqlCode = theSQL[0].replaceAll("\\s+","%20");
            final URL url = new URL(LINK + sqlCode);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final InputStream in = new BufferedInputStream(connection.getInputStream());
            final Scanner scanner = new Scanner(in).useDelimiter("\\A");
            final String response = scanner.hasNext() ? scanner.next() : "[]";
            if(DEBUG) {
                Log.d(TAG, "Response: " + response);
            }
            jArr = new JSONArray(response);
            connection.disconnect();
        } catch (IOException | JSONException exception) {
            Log.e(TAG, "Error: " + exception.getMessage());
        }
        return jArr;
    }
}
