package dev.eyesless.simple_weather_for_fishermans;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import dev.eyesless.simple_weather_for_fishermans.fragments.CentralFragmentImpl;

public class AMainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_amain;
    private Toolbar mytoolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawer;
    private NavigationView naview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        frameRemoover(new CentralFragmentImpl(), "ButtonsMain");

        initNavigationView();

        inittoolbar();

        initDrawerTogle ();
    }

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

    //код Drawer Togle кнопка выдвижения и задвижения drawer-а
    private ActionBarDrawerToggle initDrawerTogle() {

        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_closed){
            public void onDrawerClosed (View v) {
                super.onDrawerClosed(v);
            }
            public void onDrawerOpened (View v) {
                super.onDrawerOpened(v);
            }
        };

        return drawerToggle;
    }

    //main method for remoove frames when clicked
    public void frameRemoover(Fragment fragment, String mytag) {

        android.support.v4.app.FragmentTransaction fratramain = getSupportFragmentManager().beginTransaction();
        fratramain.replace(R.id.replaced_main, fragment, mytag);
        fratramain.addToBackStack(null);
        fratramain.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fratramain.commit();

    }

}
