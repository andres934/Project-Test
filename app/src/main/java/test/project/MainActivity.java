package test.project;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view) NavigationView mNavView;
    public static int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        userId = i.getIntExtra("userId",0);
        ButterKnife.bind(this);
        setToolbar();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setNavigationIcon();
            }
        });

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment f;

                if (!item.isChecked())
                    item.setChecked(true);

                mDrawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.Home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new Home()).commit();
                        return true;
                    case R.id.item_crear:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new addProduct()).commit();
                        return true;
                    case R.id.item_lista:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new listProducts()).commit();
                        return true;
                    case R.id.item_busqueda_barra:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new searchByBarcode()).commit();
                        return true;
                    case R.id.Exit:
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        SharedPreferences prefs = getSharedPreferences("UserId",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("userId");
                        editor.commit();
                        finish();

                        return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new Home()).commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.hint_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("QUERY", s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setNavigationIcon() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        ActionBar ab = getSupportActionBar();

        if (backStackEntryCount == 0)
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        else
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

                if (backStackEntryCount == 0)
                    mDrawerLayout.openDrawer(GravityCompat.START);
                else
                    super.onBackPressed();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
