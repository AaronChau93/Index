package com.aaron.chau.index.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.chau.index.MainActivity;
import com.aaron.chau.index.R;
import com.aaron.chau.index.activities.ItemDetailActivity;
import com.aaron.chau.index.models.IndexCol;
import com.aaron.chau.index.models.Item;
import com.aaron.chau.index.models.ItemDescription;
import com.aaron.chau.index.models.UserInventory;
import com.aaron.chau.index.models.UserItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements Serializable {
    private static final String TAG = ItemDetailFragment.class.getName();
    private static final String IDF_EDITING = "isEditing";

    public static final String USER_ITEM_ID = "ITEM";
    public static final String INVENTORY_ITEM_ID = "INV_ITEM";

    private UserItem mItem;

    private Map<IndexCol, View> myTextViews;
    private Map<IndexCol, View> myEditTexts;
    private boolean myIsEditing;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            myIsEditing = savedInstanceState.getBoolean(IDF_EDITING);
        }

        if (getArguments().containsKey(USER_ITEM_ID)) {
            if (getArguments().getInt(USER_ITEM_ID) >= 0) {
                mItem = UserInventory.USER_ITEM_MAP.get(getArguments().getInt(USER_ITEM_ID));

                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout =
                        (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.item.itemName);
                }
            } else {
                Toast.makeText(getContext(), "An error has occured while " +
                        "loading your content.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "View created.");
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        myTextViews = new TreeMap<>();
        myEditTexts = new TreeMap<>();

        if (mItem != null) {
            addView(rootView, IndexCol.ITEM_NAME, R.id.item_detail_itemName, mItem.item.itemName);
            addView(rootView, IndexCol.ITEM_DESC, R.id.item_detail_description,
                    mItem.item.itemDescription.description);
            addView(rootView, IndexCol.ITEM_BARCODE, R.id.item_detail_barcode,
                    mItem.item.barcodeNum);
            addView(rootView, IndexCol.ITEM_PURPRICE, R.id.item_detail_purchasePrice,
                    MainActivity.currencyFormat(mItem.purchasePrice));
            addView(rootView, IndexCol.ITEM_MSRP, R.id.item_detail_msrp,
                    MainActivity.currencyFormat(mItem.item.msrpPrice));
            addView(rootView, IndexCol.ITEM_PURDATE, R.id.item_detail_purchaseDate,
                    mItem.purchaseDate);
            addView(rootView, IndexCol.ITEM_COND, R.id.item_detail_itemCondition,
                    mItem.itemCondition);
            addView(rootView, IndexCol.ITEM_WARRANDATE, R.id.item_detail_warrantyDate,
                    mItem.warrantyDate);
            addView(rootView, IndexCol.ITEM_LENTTO, R.id.item_detail_lentTo, "N/A");
            addView(rootView, IndexCol.ITEM_LENTON, R.id.item_detail_lentToDate, "N/A");
        }

        if (myIsEditing) {
            myIsEditing = false;
            toggleEdit();
        }

        return rootView;
    }

    public void addView(View rootView, IndexCol flag, int viewId, String content) {
        final String viewIdName = getResources().getResourceName(viewId);
        final int editId = getResources()
                .getIdentifier(viewIdName + "Edit", "layout", getContext().getPackageName());

        final TextView tv = (TextView) rootView.findViewById(viewId);
        final EditText et = (EditText) rootView.findViewById(editId);

        tv.setText(content);
        et.setText(content);

        myTextViews.put(flag, tv);
        myEditTexts.put(flag, et);
    }

    public void toggleEdit() {
        if (myIsEditing) {
            saveData();
        }
        myIsEditing = !myIsEditing;

        for(IndexCol key : myEditTexts.keySet()) {
            myEditTexts.get(key).setVisibility(myIsEditing ? View.VISIBLE : View.GONE);
            myTextViews.get(key).setVisibility(myIsEditing ? View.GONE : View.VISIBLE);
        }
    }

    private void saveData() {
        for(IndexCol key : myEditTexts.keySet()) {
            final TextView tv = (TextView) myTextViews.get(key);
            final EditText et = (EditText) myEditTexts.get(key);
            if (!tv.getText().equals(et.getText())) {
                tv.setText(et.getText());
            }
        }

        final Item item = mItem.item;
        final ItemDescription itemDescription = item.itemDescription;

        item.itemName = getText(myTextViews.get(IndexCol.ITEM_NAME));
        itemDescription.description = getText(myTextViews.get(IndexCol.ITEM_NAME));
        item.barcodeNum = getText(myTextViews.get(IndexCol.ITEM_BARCODE));
        mItem.purchasePrice = new BigDecimal(getText(myTextViews.get(IndexCol.ITEM_PURPRICE)));
        item.msrpPrice = new BigDecimal(getText(myTextViews.get(IndexCol.ITEM_MSRP)));
        mItem.purchaseDate = getText(myTextViews.get(IndexCol.ITEM_PURDATE));
        mItem.itemCondition = getText(myTextViews.get(IndexCol.ITEM_COND));
        mItem.warrantyDate = getText(myTextViews.get(IndexCol.ITEM_WARRANDATE));

        mItem.update();
        item.update();
        itemDescription.update();
    }

    private String getText(View textview) {
        return ((TextView) textview).getText().toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IDF_EDITING, myIsEditing);
        super.onSaveInstanceState(outState);
    }
}
