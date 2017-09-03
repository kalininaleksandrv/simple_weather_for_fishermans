package dev.eyesless.simple_weather_for_fishermans;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import dev.eyesless.simple_weather_for_fishermans.fragments.CentralFragmentImpl;

public class AMainActivity extends AppCompatActivity implements AMainIntwerface {

    private static final int LAYOUT = R.layout.activity_amain;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Toolbar mytoolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawer;
    private NavigationView naview;
    AMainPresenter presenter;

    public AMainActivity() {
        presenter = new AMainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        if (savedInstanceState == null) {
            frameRemoover(new CentralFragmentImpl(), "Central");
        }

        initNavigationView();

        inittoolbar();

        initDrawerTogle ();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    //create navigation view and plugged custom menu (res/menu) and header
    private void initNavigationView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        naview = (NavigationView) findViewById(R.id.navigation_view);
        naview.getMenu().clear();
        naview.inflateMenu(R.menu.menu_navigation);
        naview.inflateHeaderView(R.layout.navigation_header);
    }

    //create custom toolbar
    private void inittoolbar() {
        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
    }

    //Ddrawer togle on and of listner
    private void initDrawerTogle() {

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_closed){
            public void onDrawerClosed (View v) {
                super.onDrawerClosed(v);
                Log.i("MY_TAG", "close");
            }
            public void onDrawerOpened (View v) {
                super.onDrawerOpened(v);
                Log.i("MY_TAG", "open");
            }
        };

        drawer.addDrawerListener(drawerToggle);

        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //get drawer closed by click on item (condition to open drawer by click on hamburger)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;}
             uppermenuselector (item.getItemId());

        //here is plase to handle another items on uper menu
        return super.onOptionsItemSelected(item);
    }

    public void uppermenuselector(int itemId) {
        presenter.setmenuid (itemId);
    }

    // Sync the toggle state after onRestoreInstanceState has occurred.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    //unnown magic with configuration changes
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //set up upper custom menu (res/menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //main method for remoove frames when clicked
    public void frameRemoover(Fragment fragment, String mytag) {

        android.support.v4.app.FragmentTransaction fratramain = getSupportFragmentManager().beginTransaction();
        fratramain.replace(R.id.replaced_main, fragment, mytag);
        fratramain.addToBackStack(null);
        fratramain.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fratramain.commit();

    }

    //making toasts
    @Override
    public void toastmaker(String s) {
        final Toast myToast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
        myToast.setGravity(Gravity.CENTER, 0, 30);
        myToast.show();
    }

    //check the avaliability of google play services because app using Google Places API
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }


}
