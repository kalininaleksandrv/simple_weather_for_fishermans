package dev.eyesless.simple_weather_for_fishermans;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
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

import dev.eyesless.simple_weather_for_fishermans.fragments.AboutDialogFragment;
import dev.eyesless.simple_weather_for_fishermans.fragments.CentralFragmentImpl;

public class AMainActivity extends AppCompatActivity implements AMainIntwerface, NavigationView.OnNavigationItemSelectedListener {

    private static final int LAYOUT = R.layout.activity_amain;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawer;
    private final AMainPresenter presenter;

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
        //pass context to presenret, also see "onPause"
        presenter.setActivity(this);
        isFirstLounch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //protect memory leak
        presenter.setNullToActivity();
    }

    //create navigation view and plugged custom menu (res/menu) and header
    private void initNavigationView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        NavigationView naview = (NavigationView) findViewById(R.id.navigation_view);
        naview.getMenu().clear();
        naview.inflateMenu(R.menu.menu_navigation);
        naview.inflateHeaderView(R.layout.navigation_header);
        naview.setNavigationItemSelectedListener(this);
    }

    //create custom toolbar
    private void inittoolbar() {
        Toolbar mytoolbar = (Toolbar) findViewById(R.id.toolbar);
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

    //handle user select in drawer menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_navigation_about: {
                showDialogAbout();
                break;
            }
            case R.id.menu_navigation_mailtodevs: {
                sendingEmail();
                break;
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void uppermenuselector(int itemId) {
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

    private void isFirstLounch() {
       presenter.isFirstLounch();
    }


    //main method for remoove frames when clicked
    private void frameRemoover(Fragment fragment, String mytag) {
        android.support.v4.app.FragmentTransaction fratramain = getSupportFragmentManager().beginTransaction();
        fratramain.replace(R.id.replaced_main, fragment, mytag);
        fratramain.addToBackStack(null);
        fratramain.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fratramain.commit();
    }

    //create dialog window "about programm", this method could be call whether from AMainActivity or from AmainPresenter;
    @Override
    public void showDialogAbout (){
        //first check if dialog is already create, for example, if orientation was changed, if it does - dissmiss it;
        DialogFragment dialogFragment = (DialogFragment)getSupportFragmentManager().findFragmentByTag("aboutdialog");
        if (dialogFragment != null) { dialogFragment.dismiss();}
        //second create new dialog
        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.show(getSupportFragmentManager(), "aboutdialog");
        aboutDialogFragment.setCancelable(false);
    }

    //making toasts
    @Override
    public void toastmaker(String s) {
        final Toast myToast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
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

    //email sending functionality
    private void sendingEmail() {
            try {
                Intent myintent = ShareCompat.IntentBuilder.from(AMainActivity.this)
                        .setEmailTo(new String[]{Keys.EMAIL_ADRESS})
                        .setSubject(getString(R.string.email_subj))
                        .setType("text/plain")
                        .getIntent();
                startActivity(myintent);
            } catch (ActivityNotFoundException e) {
                toastmaker(getString(R.string.nosuchactivity));
            }
    }
}
