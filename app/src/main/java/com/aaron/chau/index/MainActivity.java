package com.aaron.chau.index;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.chau.index.activities.BarcodeOrManualActivity;
import com.aaron.chau.index.activities.ItemDetailActivity;
import com.aaron.chau.index.activities.Login;
import com.aaron.chau.index.fragments.ItemDetailFragment;
import com.aaron.chau.index.models.OnFragmentInteractionListener;
import com.aaron.chau.index.models.UserInventory;
import com.aaron.chau.index.models.UserItem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item purchaseDate. On tablets, the activity presents the list of items and
 * item purchaseDate side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getName();
    private UserInventory myUserInventory;
    private static int userId;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainAppToolbar);
        setSupportActionBar(toolbar);

        userId = getSharedPreferences("userSession", Context.MODE_PRIVATE).getInt("userId", -1);
        new UserInventory();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editItemFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, BarcodeOrManualActivity.class);
                context.startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        // The detail container view will be present only in the
        // large-screen layouts (res/values-w900dp).
        // If this view is present, then the
        // activity should be in two-pane mode.
        mTwoPane = findViewById(R.id.item_detail_container) != null;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if(UserInventory.contentIsReady) {
            recyclerView.setAdapter(new MainIndexRecyclerViewAdapter(UserInventory.ITEMS));
        }
    }

    public class MainIndexRecyclerViewAdapter
            extends RecyclerView.Adapter<MainIndexRecyclerViewAdapter.ViewHolder> {

        private final List<UserItem> mValues;

        public MainIndexRecyclerViewAdapter(List<UserItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mUserItem = mValues.get(position);
            holder.mItemName.setText(mValues.get(position).item.itemName);
            holder.mItemPurchasePrice.setText(currencyFormat(mValues.get(position).purchasePrice));
            holder.mItemPurchaseDate.setText(mValues.get(position).purchaseDate);
            holder.mItemCondition.setText(mValues.get(position).itemCondition);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(ItemDetailFragment.USER_ITEM_ID, holder.mUserItem.userItemId);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.USER_ITEM_ID,
                                holder.mUserItem.userItemId);
                        intent.putExtra(ItemDetailFragment.INVENTORY_ITEM_ID,
                                UserInventory.getIdByUserItem(holder.mUserItem));
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView mItemName;
            public TextView mItemPurchasePrice;
            public TextView mItemPurchaseDate;
            public TextView mItemCondition;
            public UserItem mUserItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mItemName = (TextView) view.findViewById(R.id.item_list_content_itemName);
                mItemPurchasePrice = (TextView) view.findViewById(R.id.item_list_content_purchasePrice);
                mItemPurchaseDate = (TextView) view.findViewById(R.id.item_list_content_purchaseDate);
                mItemCondition = (TextView) view.findViewById(R.id.item_list_content_itemCondition);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItemName.getText() + "'";
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentInteraction(Uri uri) {};

    public static String currencyFormat(BigDecimal num) {
        return new DecimalFormat().format(num);
    }

    public static int getUserId() {
        return userId;
    }

    private void logout() {
        SharedPreferences userPref = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor userPrefEditor = userPref.edit();
        userPrefEditor.remove("username");
        userPrefEditor.remove("sessionId");
        userPrefEditor.apply();
        Intent backToLogin = new Intent(this, Login.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backToLogin);
        Toast.makeText(this, "Logged out!", Toast.LENGTH_LONG).show();
        finish();
    }
}
