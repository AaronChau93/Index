package com.aaron.chau.index.models;

import android.os.AsyncTask;
import android.util.Log;

import com.aaron.chau.index.models.MySqlViaPHP;
import com.aaron.chau.index.models.UserItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Helper class for providing sample purchasePrice for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class UserInventory {
    private static final String TAG = UserInventory.class.getName();

    /**
     * An array of sample UserItems items.
     */
    public static final List<UserItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample UserItems items, by ID.
     */
    public static final Map<Integer, UserItem> ITEM_MAP = new HashMap<>();

    public static boolean contentIsReady;

    static {
        refresh();
    }

    private static void addItem(UserItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.userItemId, item);
    }

    public static void refresh() {
        contentIsReady = false;
        // Query for user items.
        try {
            ITEMS.clear();
            ITEM_MAP.clear();
            JSONArray results = new MySqlViaPHP().execute(
                    "SELECT * FROM UserItems" //Where userid = ...
            ).get();
            for (int i = 0; i < results.length(); i++) {
                addItem(new UserItem(results.getJSONObject(i)));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        } finally {
            contentIsReady = true;
        }
    }
}
