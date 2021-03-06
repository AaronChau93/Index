package com.aaron.chau.index.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron Chau on 2/28/2016.
 */
public class ItemDescription implements Serializable {
    private static final String ID_ID = "itemDescriptionId";
    private static final String ID_DESCRIPTION = "description";
    public static final Map<Integer, ItemDescription> ITEM_DESC_MAP = new HashMap<>();

    public int itemDescriptionId;
    public String description;

    public ItemDescription(final JSONObject theDescription) throws JSONException {
        this(theDescription.getInt(ID_ID), theDescription.getString(ID_DESCRIPTION));
    }

    public ItemDescription(int itemDescriptionId, String description) {
        this.itemDescriptionId = itemDescriptionId;
        this.description = description;
        ITEM_DESC_MAP.put(itemDescriptionId, this);
    }

    public boolean update() {
        new MySqlViaPHP().execute(
                "UPDATE ItemDescriptions " +
                        "SET " + ID_DESCRIPTION + "=\"" + description + "\"" +
                        "WHERE " + ID_ID + "=" + itemDescriptionId
        );
        return true;
    }
}
