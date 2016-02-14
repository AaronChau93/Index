package com.aaron.chau.index.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.aaron.chau.index.R;

public class AddItemActivity extends AppCompatActivity {
    private EditText myBarcodeET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);

        // Get views
        myBarcodeET = (EditText) findViewById(R.id.itemBarcode);


        final Bundle bundle = getIntent().getExtras();
        if(!bundle.isEmpty()) {
            myBarcodeET.setText(bundle.getString("barcode"));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.save_item_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
