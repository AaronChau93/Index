package com.aaron.chau.index.models;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * Created by Aaron Chau on 2/28/2016.
 */
public class UserItem implements Serializable {
    private static final String UI_ID = "userItemId";
    private static final String UI_ITEMID = "itemId";
    private static final String UI_PURCHASEPRICE = "purchasePrice";
    private static final String UI_PURCHASEDATE = "purchaseDate";
    private static final String UI_REBATE = "rebate";
    private static final String UI_WARRANTYDATE = "warrantyDate";
    private static final String UI_CONDITION = "itemCondition";

    private AsyncTask<String, Void, JSONArray> mySQL;

    public final int userItemId;
    public final int itemId;
    public BigDecimal purchasePrice;
    public String purchaseDate;
    public BigDecimal rebate;
    public String warrantyDate;
    public String itemCondition;
    public Item item;

    public UserItem(final JSONObject userItem) throws JSONException {
        this(userItem.getInt(UI_ID),
                userItem.getInt(UI_ITEMID),
                userItem.getString(UI_PURCHASEPRICE),
                userItem.getString(UI_PURCHASEDATE),
                userItem.getString(UI_REBATE),
                userItem.getString(UI_WARRANTYDATE),
                userItem.getString(UI_CONDITION));
    }

    public UserItem(int userItemId, int itemId) {
        this(userItemId, itemId, "", "", "", "", "");
    }

    public UserItem(int userItemId, int itemId, String purchasePrice,
                    String purchaseDate, String itemCondition) {
        this(userItemId, itemId, purchasePrice, purchaseDate, "", "", itemCondition);
    }

    public UserItem(int userItemId, int itemId, String purchasePrice,
                    String purchaseDate, String rebate, String warrantyDate, String itemCondition) {
        this.userItemId = userItemId;
        this.itemId = itemId;
        this.purchasePrice = new BigDecimal(purchasePrice);
        this.purchaseDate = purchaseDate;
        this.rebate = new BigDecimal(rebate);
        this.warrantyDate = warrantyDate;
        this.itemCondition = itemCondition;

        mySQL = new MySqlViaPHP();
        try {
            if (Item.ITEM_MAP.containsKey(itemId)) {
                this.item = Item.ITEM_MAP.get(itemId);
            } else {
                JSONArray results = mySQL.execute(
                        "SELECT * FROM Items WHERE itemId = " + itemId
                ).get();
                this.item = new Item(results.getJSONObject(0));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean update() {
        new MySqlViaPHP().execute(
                "UPDATE UserItems " +
                "SET " + UI_PURCHASEPRICE + "=" + purchasePrice.toString() +
                "," + UI_PURCHASEDATE + "='" + purchaseDate + "'" +
                "," + UI_REBATE + "=" + rebate +
                "," + UI_WARRANTYDATE + "='" + warrantyDate + "'" +
                "," + UI_CONDITION + "='" + itemCondition + "' " +
                "WHERE userItemId=" + userItemId
        );
        return true;
    }

    @Override
    public String toString() {
        return "Name: " + item.itemName + ", purchasePrice: " + purchasePrice + ", purchaseDate: "
                + purchaseDate + ", itemCondition: " + itemCondition;
    }
}