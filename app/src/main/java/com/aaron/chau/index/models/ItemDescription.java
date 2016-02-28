package com.aaron.chau.index.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Chau on 2/28/2016.
 */
public class ItemDescription {
    private static final String ID_ID = "itemDescriptionId";
    private static final String ID_DESCRIPTION = "description";

    public int itemDescriptionId;
    public String description;

    public ItemDescription(final JSONObject theDescription) throws JSONException {
        this(theDescription.getInt(ID_ID), theDescription.getString(ID_DESCRIPTION));
    }

    public ItemDescription(int itemDescriptionId, String description) {
        this.itemDescriptionId = itemDescriptionId;
        this.description = description;
    }
}
