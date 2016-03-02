package com.aaron.chau.index.models;

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
    public static final Map<Integer, UserItem> USER_ITEM_MAP = new HashMap<>();

    public static boolean contentIsReady;

    static {
        refresh();
    }

    private static void addItem(UserItem item) {
        if (!USER_ITEM_MAP.containsKey(item.userItemId)) {
            ITEMS.add(item);
            USER_ITEM_MAP.put(item.userItemId, item);
        }
    }

    public static void refresh() {
        contentIsReady = false;
        // Query for user items.
        try {
            JSONArray results = new MySqlViaPHP().execute(
                    "SELECT * FROM UserItems" //Where userid = ...
            ).get();
            for (int i = 0; i < results.length(); i++) {
                JSONObject userItem = results.getJSONObject(i);
                if (!USER_ITEM_MAP.containsKey(userItem.getInt("userItemId"))) {
                    addItem(new UserItem(userItem));
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        } finally {
            contentIsReady = true;
        }
    }
}
