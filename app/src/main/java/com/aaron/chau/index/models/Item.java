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
public class Item implements Serializable {
    private static final String I_ID = "itemId";
    private static final String I_BARCODE = "barcodeNum";
    private static final String I_NAME = "itemName";
    private static final String I_DESCRIPTIONID = "itemDescriptionId";
    private static final String I_MSRP = "msrpPrice";

    public final int itemId;
    public String barcodeNum;
    public String itemName;
    public final int itemDescriptionId;
    public BigDecimal msrpPrice;
    public ItemDescription itemDescription;

    public Item(final JSONObject theItem) throws JSONException {
        this(theItem.getInt(I_ID),
                theItem.getString(I_BARCODE),
                theItem.getString(I_NAME),
                theItem.getInt(I_DESCRIPTIONID),
                theItem.getString(I_MSRP));
    }

    public Item(int itemId, String barcodeNum, String itemName, int itemDescriptionId,
                String msrpPrice) {
        this.itemId = itemId;
        this.barcodeNum = barcodeNum;
        this.itemName = itemName;
        this.itemDescriptionId = itemDescriptionId;
        this.msrpPrice = new BigDecimal(msrpPrice);

        try {
            JSONArray results = new MySqlViaPHP().execute(
                    "SELECT * FROM ItemDescriptions WHERE itemDescriptionId = " + itemDescriptionId
            ).get();
            this.itemDescription = new ItemDescription(results.getJSONObject(0));
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean update() {
        new MySqlViaPHP().execute(
                "UPDATE Items " +
                "SET " + I_BARCODE + "=" + barcodeNum +
                "," + I_NAME + "=\"" + itemName + "\"" +
                "," + I_MSRP + "=" + msrpPrice.toString() + " " +
                "WHERE " + I_ID + "=" + itemId
        );
        return true;
    }
}
