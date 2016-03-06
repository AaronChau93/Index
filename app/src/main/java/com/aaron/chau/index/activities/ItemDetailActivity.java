package com.aaron.chau.index.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.aaron.chau.index.MainActivity;
import com.aaron.chau.index.R;
import com.aaron.chau.index.fragments.ItemDetailFragment;
import com.aaron.chau.index.models.MySqlViaPHP;
import com.aaron.chau.index.models.UserInventory;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item purchaseDate are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {
    private static final String TAG = ItemDetailActivity.class.getName();
    private ItemDetailFragment idFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(ItemDetailFragment.USER_ITEM_ID,
                    getIntent().getIntExtra(ItemDetailFragment.USER_ITEM_ID, -1));
            idFragment = new ItemDetailFragment();
            idFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, idFragment, ItemDetailFragment.class.getName())
                    .commit();
        } else {
            idFragment = (ItemDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(ItemDetailFragment.class.getName());
        }

        final FloatingActionButton deleteFab = (FloatingActionButton) findViewById(R.id.deleteItemFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogListener();

                AlertDialog.Builder ab = new AlertDialog.Builder(ItemDetailActivity.this);
                ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        FloatingActionButton editFab = (FloatingActionButton) findViewById(R.id.editItemFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idFragment.toggleEdit();
                if (deleteFab.getVisibility() == View.GONE) {
                    deleteFab.setVisibility(View.VISIBLE);
                    ((ImageButton) view).setImageResource(android.R.drawable.ic_menu_save);
                } else {
                    deleteFab.setVisibility(View.GONE);
                    ((ImageButton) view).setImageResource(android.R.drawable.ic_menu_edit);
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Do your Yes progress
                    // delete this userItemId
                    final int userItemId = getIntent().getIntExtra(ItemDetailFragment.USER_ITEM_ID, -1);
                    final int invItemId = getIntent().getIntExtra(ItemDetailFragment.INVENTORY_ITEM_ID, -1);
                    if (userItemId != -1)
                        new MySqlViaPHP().execute(
                                "DELETE FROM UserItems " +
                                        "WHERE userItemId = " + userItemId
                        );

                    if (invItemId != -1)
                        new MySqlViaPHP().execute(
                                "DELETE FROM Inventory " +
                                        "WHERE Inventory.inventoryId = " + invItemId
                        );
                    UserInventory.remove(invItemId);
                    Intent backToMain = new Intent(ItemDetailActivity.this, MainActivity.class);
                    backToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(backToMain);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //Do your No progress
                    break;
            }
        }
    }
}
