package com.aaron.chau.index.models;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Aaron Chau on 2/28/2016.
 */
public class UPCLookup extends AsyncTask<String, Void, Map<String, String>> {
    private static final boolean DEBUG = false;
    private static final String TAG = UPCLookup.class.getName();
    private static final String LINK = "http://www.digit-eyes.com/cgi-bin/digiteyes.cgi?upcCode=";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String BARCODE = "barcode";

    @Override
    protected Map<String, String> doInBackground(String... theBarcode) {
        Map<String, String> details = new HashMap<>();
        String code = theBarcode[0];
        try {

            final URL url = new URL(LINK + code);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final InputStream in = new BufferedInputStream(connection.getInputStream());
            final Scanner scanner = new Scanner(in);
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) response.append(scanner.next() + " ");

            // TODO: Check if response contains an error.
            details.put(TITLE, getTitle(response));
            details.put(DESCRIPTION, getDescription(response));
            details.put(BARCODE, code);
            if (DEBUG) {
                Log.d(TAG, "Response: " + response);
                Log.d(TAG, "Title: " + getTitle(response));
                Log.d(TAG, "Description: " + getDescription(response));
            }
            connection.disconnect();
        } catch (IOException exception) {
            Log.e(TAG, "Error: " + exception.getMessage());
        }
        return details;
    }

    private String getTitle(StringBuilder response) {
        final String head = "<b>";
        final String tail = "</b>";
        final int start = response.indexOf(head) + head.length();
        final int end = response.indexOf(tail);
        return cleanString(response.substring(start, end));
    }

    private String getDescription(StringBuilder response) {
        final String head = "<br><br>";
        final String tail = "</td></tr> <tr>";
        final int start = response.indexOf(head) + head.length();
        final int end = response.indexOf(tail);
        return cleanString(response.substring(start, end));
    }

    public static String cleanString(String string) {
        // The following characters can break a sql query. That is why we remove them.
        return string.replaceAll("<[^>]*>", "")   // Removes html tags
                     .replaceAll("&", ";amp;")    // Remove ampersands
                     .replaceAll("#", ";pound;"); // Removes pounds symbol
    }
}
