package com.aaron.chau.index.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.aaron.chau.index.MainActivity;
import com.aaron.chau.index.R;
import com.aaron.chau.index.models.MySqlViaPHP;
import com.aaron.chau.index.models.UPCLookup;
import com.aaron.chau.index.models.UserInventory;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = AddItemActivity.class.getName();
    private EditText myItemNameET;
    private EditText myDescriptionET;
    private EditText myBarcodeET;
    private EditText myPurchasePriceET;
    private EditText myMsrpET;
    private EditText myPurchaseDateET;
    private EditText myItemConditionET;
    private EditText myrebateET;
    private EditText myWarrantyDateET;
    private Runnable delayRun;
    private Handler delayHandle;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);

        // Get views
        myItemNameET = (EditText) findViewById(R.id.item_form_itemNameEdit);
        myDescriptionET = (EditText) findViewById(R.id.item_form_descriptionEdit);
        myBarcodeET = (EditText) findViewById(R.id.item_form_barcodeEdit);
        myPurchasePriceET = (EditText) findViewById(R.id.item_form_purchasePriceEdit);
        myMsrpET = (EditText) findViewById(R.id.item_form_msrpEdit);
        myPurchaseDateET = (EditText) findViewById(R.id.item_form_purchaseDateEdit);
        myItemConditionET = (EditText) findViewById(R.id.item_form_itemConditionEdit);
        myrebateET = (EditText) findViewById(R.id.item_form_rebateEdit);
        myWarrantyDateET = (EditText) findViewById(R.id.item_form_warrantyDateEdit);


        final Bundle bundle = getIntent().getExtras();
        if(bundle != null && !bundle.isEmpty()) {
            myItemNameET.setText(bundle.getString(UPCLookup.TITLE));
            myDescriptionET.setText(bundle.getString(UPCLookup.DESCRIPTION));
            myBarcodeET.setText(bundle.getString(UPCLookup.BARCODE));
        }

        progressDialog = new ProgressDialog(AddItemActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Saving...");
        progressDialog.setMessage("Please wait while Index saves your item.");
        delayHandle = new Handler();
        delayRun = new Runnable() {
            @Override
            public void run() {
                saveItem();
                progressDialog.dismiss();
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.save_item_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Snackbar.make(view, "Saving, please wait...", Snackbar.LENGTH_INDEFINITE)
//                        .setAction("Action", null).show();
                progressDialog.show();
                view.setEnabled(false);
                delayHandle.postDelayed(delayRun, 1000);
                view.setEnabled(true);

            }
        });
    }

    private void saveItem() {
        try {
            int descId = getDescriptionId();
            int itemId = getItemId(descId);
            int userItemId = getUserItemId(itemId);

            // save into user inventory
            new MySqlViaPHP().execute(
                    "INSERT INTO Inventory(ownerId, userItemId) " +
                            "VALUES (" + MainActivity.getUserId() + "," + userItemId + ")"
            ).get();

            UserInventory.refresh();

            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        } finally {

        }
    }

    private int getUserItemId(int itemId) throws ExecutionException, InterruptedException, JSONException {
        String purchasePrice = view2String(myPurchasePriceET);
        String purchaseDate = view2String(myPurchaseDateET);
        String rebate = view2String(myrebateET);
        String warrantyDate = view2String(myWarrantyDateET);
        String itemCondition = view2String(myItemConditionET);
        // Todo: Replace the strings with empty quotes if length is 0. (or isEmpty());
        // Todo: Also do that to the rest of the methods.

        JSONArray results = new MySqlViaPHP().execute(
                "INSERT INTO UserItems (itemId, purchasePrice, purchaseDate, rebate, warrantyDate, itemCondition) " +
                        "VALUES (" + itemId + "," + purchasePrice + "," + purchaseDate +
                                "," + rebate + "," + warrantyDate + "," + itemCondition + ")"
        ).get();
        return results.getJSONObject(0).getInt("id");
    }

    private int getItemId(int descId) throws ExecutionException, InterruptedException, JSONException {
        final String barcode = view2String(myBarcodeET);
        final String itemName = view2String(myItemNameET);
        final String msrp = view2String(myMsrpET);
        final int itemId;

        // Do a query
        JSONArray results = new MySqlViaPHP().execute(
            "SELECT * " +
                    "FROM Items " +
                    "WHERE barcodeNum = " + barcode +
                    " AND itemName = " + itemName +
                    " AND itemDescriptionId = " + descId +
                    " AND msrpPrice = " + msrp
        ).get();
        if(results.length() == 0) {
            // If the item does not exist then create it and then set itemId as the newly created
            // item id.
            results = new MySqlViaPHP().execute(
                  "INSERT INTO Items (barcodeNum, itemName, itemDescriptionId, msrpPrice) \n" +
                      "VALUES (" + barcode + "," + itemName + "," + descId + "," + msrp + ")"
            ).get();
            itemId = results.getJSONObject(0).getInt("id");
        } else {
            // else we just use the already existing item.
            itemId = results.getJSONObject(0).getInt("itemId");
        }
        return itemId;
    }

    private int getDescriptionId() throws ExecutionException, InterruptedException, JSONException {
        final String desc = view2String(myDescriptionET);
        final int descId;
//        Log.d(TAG, desc);
        // Do a query
        JSONArray results = new MySqlViaPHP().execute(
                "SELECT * " +
                        "FROM ItemDescriptions " +
                        "WHERE description=" + desc
        ).get();
        if(results.length() == 0) {
            // If the description does not exist then create it and then set descId as the newly
            // created description.
            results = new MySqlViaPHP().execute(
                    "INSERT INTO ItemDescriptions (description) " +
                            "VALUES (" + desc + ")"
            ).get();
            descId = results.getJSONObject(0).getInt("id");
        } else {
            // Otherwise use the already existing description.
            descId = results.getJSONObject(0).getInt("itemDescriptionId");
        }
        return descId;
    }

    public static String view2String(View view) {

        if (view instanceof EditText) {
            return addQuotes2String(UPCLookup.cleanString(((EditText) view).getText().toString().trim()));
        }

        return addQuotes2String("");
    }

    public static String addQuotes2String(String theString) {
        return "\"" + theString + "\"";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
