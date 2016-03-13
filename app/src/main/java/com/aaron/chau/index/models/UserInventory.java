package com.aaron.chau.index.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.aaron.chau.index.MainActivity;
import com.aaron.chau.index.activities.AddItemActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

/**
 * Helper class for providing sample purchasePrice for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public final class UserInventory {
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

    public UserInventory() {
        ITEMS.clear();
        USER_ITEM_MAP.clear();
        refresh();
    }

    public static void addItem(UserItem item) {
        if (item != null && !USER_ITEM_MAP.containsKey(item.userItemId)) {
            ITEMS.add(item);
            USER_ITEM_MAP.put(item.userItemId, item);
        }
    }

    public static void remove(int inventoryItemId) {
        if (inventoryItemId != -1) {
            ITEMS.remove(USER_ITEM_MAP.remove(inventoryItemId));
        }
    }

    public static int getIdByUserItem(UserItem item) {
        if (item != null) {
            for(int invId : USER_ITEM_MAP.keySet()) {
                if (USER_ITEM_MAP.get(invId).userItemId == item.userItemId) {
                    return invId;
                }
            }
        }
        return -1;
    }

    public static void refresh() {
        contentIsReady = false;
        // Query for user items.
        try {
            JSONArray results = new MySqlViaPHP().execute(
                    // Consider doing a huge join and just use that instead.
                    "SELECT * " +
                            "FROM Inventory JOIN UserItems " +
                                "ON Inventory.userItemId = UserItems.userItemId " +
                            "WHERE Inventory.ownerId = " + MainActivity.getUserId()
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
