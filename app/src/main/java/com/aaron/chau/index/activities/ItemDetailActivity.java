package com.aaron.chau.index.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.aaron.chau.index.MainActivity;
import com.aaron.chau.index.R;
import com.aaron.chau.index.fragments.ItemDetailFragment;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editItemFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idFragment.toggleEdit();
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
}
