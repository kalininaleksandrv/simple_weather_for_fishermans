package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.repository.PrognosticModel;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherLoader;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherPastLoader;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public class CentralFragmentPresenter implements LoaderManager.LoaderCallbacks<List<Datum>>, LocationListener {

    private final CentralFragmentInterface cfinterface;
    private AMainActivity mActivity;
    private Context context;
    final static String DEFOULT_LOC = "First, Lounch";
    private String autocompleted;
    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static String COORDINATES_IN_BUNDLE = "coords";
    public final static String LOCATION_IN_BUNDLE = "locationinbundle";
    private LoaderManager mLoader;
    private List<Datum> midlist;
    private boolean isupdate = false;
    private boolean isLoaderExist = false;
    private SharedPreferences sharedpref;
    private final static String DATAPREFSNAME = "dataprefsname";
    private final static String DATASAVEDSTRING = "datasavedstring";

    private Double defoultLAT = 55.755826;
    private Double defoultLNG = 37.6172999;
    private final static String PREFSNAME = "prefsname";
    private String defoult_city = "Москва, Россия";
    private final static String SAVEDSTRING = "savedstr";
    private final static String SAVEDLOC = "savedloc";
    private final static String SAVEDTIME = "savedtime";
    private final static long DEFTIMEOFDELAY = 3600000;

    private LocationManager locationManager;


    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;

    }

    //call when button pressed in IMPL
    void startSearch(boolean update) {

        String fix;
        //prepare to request autocompleeted place or defoult plase (Moscow, Russia)
        if (autocompleted == null) {
            if ((getFromPrefs(PREFSNAME, false) == null)) {
                fix = DEFOULT_LOC.replaceAll("\\s+", "+"); //defoult loc returns only if plase in shard prefs is empty
            } else {
                fix = getFromPrefs(PREFSNAME, false);
            }
        } else {
            fix = autocompleted.replaceAll("\\s+", "+");
        }

        //protect network from unnecessary requwest
        if (isDataObsoled(fix)) {
            getCoordinatesWithLoader(fix, update);
            Log.e("MY_TAG", "Data IS OBSOLED");
        } else {
            cfinterface.stoprefreashing();
            mActivity.toastmaker(context.getResources().getString(R.string.stoprefresh));
            Log.e("MY_TAG", "Data is NOT OBSOLED");
        }

    }

    //return true if response data new and don't need to update
    private boolean isDataObsoled(String data) {
        String newdata = data.replace("+", " ");
        String olddata = getFromPrefs(PREFSNAME, false);
        long oldtime = getTimeFromPrefs(PREFSNAME) + DEFTIMEOFDELAY;
        if (getSysTime() > oldtime || newdata == null || olddata == null) {
            return true; //in case when time intervale (between recorded in last shared prefs and current system time) more hen defoult value OR in case when time intervale less then default and wee have"nt data  to compare
        } else {
            return (!newdata.equals(olddata)); //in case when time intervale less then default, returns result inverse by check - if user request same city
        }
    }

    private void getCoordinatesWithLoader(String fix, boolean update) {

        Bundle coordinatesbundle = new Bundle();
        coordinatesbundle.putString(COORDINATES_IN_BUNDLE, fix);
        coordinatesbundle.putString(LOCATION_IN_BUNDLE, getLastLocation());
        if ((update) || (isLoaderExist)) {
            mLoader.restartLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isupdate = true;
            Log.e("MY_TAG", "just restart loader");
        } else {
            mLoader.initLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isLoaderExist = true;
        }
    }

    //return StartLocation NAME on first app lounch, then return loc NAME from prefs
    private String getLastLocation() {

        if (getFromPrefs(PREFSNAME, true) == null) {
            Log.e("MY_TAG", "NO PREFS, try to request loc from defoult ");
            return new StringBuilder().append(defoultLAT).append(",").append(defoultLNG).toString();
        } else {
            return getFromPrefs(PREFSNAME, true).replace("\"", "");
        }
    }

    //if "doweeneedlocation os false method returns SAVEDLOC means last asked location NAME, if true returns coordinates
    private String getFromPrefs(String prefname, boolean doweneedlocation) {

        SharedPreferences sharedpref = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);

        if (doweneedlocation) {
            if (sharedpref.getString(SAVEDSTRING, null) == null) {
                return null;
            } else {
                return sharedpref.getString(SAVEDSTRING, null);
            }
        } else {
            if (sharedpref.getString(SAVEDLOC, null) == null) {
                return null;
            } else {
                return sharedpref.getString(SAVEDLOC, null).replace("\"", "");
            }
        }
    }

    private long getTimeFromPrefs(String prefname) {
        SharedPreferences sharedpref = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
        return sharedpref.getLong(SAVEDTIME, 1);
    }


    private void addToPrefs(String valuecoords, String placecoords, Long reqwesttime) {
        SharedPreferences sharedpref;
        SharedPreferences.Editor editor;
        sharedpref = context.getSharedPreferences(CentralFragmentPresenter.PREFSNAME, Context.MODE_PRIVATE);

        editor = sharedpref.edit();
        Gson gson = new Gson();
        String jsonSavedString = gson.toJson(new StringBuilder().append(valuecoords).toString());
        String jsonPlaceSavedString = gson.toJson(new StringBuilder().append(placecoords).toString());
        editor.putString(SAVEDSTRING, jsonSavedString);
        editor.putString(SAVEDLOC, jsonPlaceSavedString);
        editor.putLong(SAVEDTIME, reqwesttime);
        editor.apply();
    }

    private void getPastWithLoader(List<Datum> mylist) {

        midlist = mylist;
        if (isupdate) {
            mLoader.restartLoader(R.id.past_loader_id, Bundle.EMPTY, this);
            Log.e("MY_TAG", "restart PAST loader");
        } else {
            mLoader.initLoader(R.id.past_loader_id, Bundle.EMPTY, this);
            Log.e("MY_TAG", "init PAST loader");
        }
    }

    //start intent to autocompletion location
    void startActivity(CentralFragmentImpl centralFragment) {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(mActivity);

            centralFragment.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Failed: Google Play", e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Failed: Play . n aval. ", e.getMessage());
        }
    }

    //trying to get coordinates from gps
    void getGpsPermission() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCoordinatesFromGps();
            } else {
                cfinterface.getGpsPermission();
            }
        }
            else {
            getCoordinatesFromGps();
            // TODO: 26.11.2017 must check if devise has GPS and GPS ON
        }
    }


    //here try to get coordinates from GPS
    void getCoordinatesFromGps(){

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, DEFTIMEOFDELAY, 10000, this);
            informUserAboutLastLocation();
        } catch (SecurityException | NullPointerException e) {
            informUserAboutGpsUnavaliable();
        }

    }

    private void informUserAboutLastLocation() {
        String locationProvider = LocationManager.GPS_PROVIDER;
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            mActivity.toastmaker(String.valueOf(lastKnownLocation.getLatitude())+ " " + String.valueOf(lastKnownLocation.getLongitude()));
        } catch (SecurityException e) {
            informUserAboutGpsUnavaliable();
        }

    }

    void informUserAboutGpsUnavaliable() {
        mActivity.toastmaker(context.getString(R.string.nogps));
    }

    //location listner results placed here
    @Override
    public void onLocationChanged(Location location) {
        informUserAboutLastLocation();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        mActivity.toastmaker(context.getString(R.string.gpsenabled));
    }

    @Override
    public void onProviderDisabled(String provider) {

        mActivity.toastmaker(context.getString(R.string.gpsdisabled));

    }

    private void adapterrefresh(List<Datum> mylist, boolean isdatanew, boolean remooveelements) {

        if (remooveelements) {

            for (int i = 0; i < 5; i++) {
                mylist.remove(0);
            }
        }
        cfinterface.adapterrefresh(mylist);
        cfinterface.stoprefreashing();
        Log.e("MY_TAG", "refreshing adapter on presenter " + isdatanew);
    }

    private void addListToSharedPrefs(List<Datum> mylist) {
        SharedPreferences.Editor editor;
        Gson gson = new Gson();
        Daily addeddaily = new Daily();
        mylist.get(0).setCustomlocationname(autocompleted);
        addeddaily.setData(mylist);
        editor = sharedpref.edit();
        String jsonSavedString = gson.toJson(addeddaily);
        editor.putString(DATASAVEDSTRING, jsonSavedString);
        editor.apply();
    }


    private List<Datum> getListFromPrefs() {
        Gson gson = new Gson();
        String json = sharedpref.getString(DATASAVEDSTRING, null);
        Daily restoreddaily = gson.fromJson(json, Daily.class);
        return restoreddaily.getData();
    }

    //loader callback methods
    @Override
    public Loader<List<Datum>> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case R.id.weather_loader_id:
                return new WeatherLoader(context, args);
            case R.id.past_loader_id:
                return new WeatherPastLoader(context, midlist);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished( Loader<List<Datum>> loader, List<Datum> data) {
        int id = loader.getId();
        if (id == R.id.weather_loader_id) {
            if (data != null) {
                boolean isNew = data.get(0).isNew();
                setAutocompleted(data.get(0).getCustomlocationname());
                addToPrefs(data.get(0).getCustomccordinates(), data.get(0).getCustomlocationname(), getSysTime());
                adapterrefresh(data, isNew, false);
                getPastWithLoader(data);
                Log.e("MY_TAG", "databeforeBITE " + data.get(0).getCustomlocationname());
            } else
            {
                //if internet on phone is lost close progress and show toast in CentralFragmentImpl adapterrefresh
                adapterrefresh(null, true, false);
                Log.e("MY_TAG", "geting NULL weather");
            }
        }
        if (id == R.id.past_loader_id){
            if (data != null && data.size()>8){
                List<Datum> datawithbite = new PrognosticModel(data).createBiteList();
                if (datawithbite != null){
                    addListToSharedPrefs(datawithbite);
                    adapterrefresh(datawithbite, true, true);
                    Log.e("MY_TAG", "datawithbite");
                } else {
                    //if internet on phone is lost close progress and show toast in CentralFragmentImpl adapterrefresh
                    adapterrefresh(null, true, false);
                    Log.e("MY_TAG", "prognostic model returns null");
                }
            } else {
                //if internet on phone is lost close progress and show toast in CentralFragmentImpl adapterrefresh
                adapterrefresh(null, true, false);
                Log.e("MY_TAG", "past weather loader returns null");
            }
        }
    }
    @Override
    public void onLoaderReset( Loader<List<Datum>> loader) {
        //do nothing
    }

    //setLoadManager
    void setLoadManager(LoaderManager loadmmngr) {
        this.mLoader = loadmmngr;
    }

    // set aMainActivity
    void setActivity(AMainActivity aMainActivity) {
        this.mActivity = aMainActivity;
        this.context = aMainActivity.getApplication().getApplicationContext();
        sharedpref = context.getSharedPreferences(DATAPREFSNAME, Context.MODE_PRIVATE);
    }

    void setAutocompleted(String autocompleted) {
        this.autocompleted = autocompleted;
        cfinterface.setDefoultLoc();
    }

    String getAutocompleeted() {return autocompleted;}

    //temp List to init RVAdapter in starting app
    List<Datum> getTempAdapterList() {

        if (sharedpref.getString(DATASAVEDSTRING, null) != null){
            List<Datum> listfromprefs = getListFromPrefs();
            setAutocompleted(listfromprefs.get(0).getCustomlocationname());
            adapterrefresh(listfromprefs, false, true);
           return listfromprefs;
        } else {

            Datum defoultdatum = new Datum();
            List<Datum> mydatum = new ArrayList<>();
            mydatum.add(defoultdatum);
            return mydatum;
        }
    }

    //return system time
    private long getSysTime() {
        return Calendar.getInstance().getTimeInMillis();
    }



}


