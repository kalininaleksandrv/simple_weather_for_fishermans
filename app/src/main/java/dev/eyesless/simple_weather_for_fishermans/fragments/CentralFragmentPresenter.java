package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.Keys;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.repository.PrognosticModel;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherLoader;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherPastLoader;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public class CentralFragmentPresenter implements LoaderManager.LoaderCallbacks<List<Datum>> {

    private final CentralFragmentInterface cfinterface;
    private AMainActivity mActivity;
    private Context context;
    final static String DEFOULT_LOC = "First, Lounch";
    private final static String GPS_LOC = "GPS";
    private String autocompleted;

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
    private final static String SAVEDSTRING = "savedstr"; //its LAT and LNG coordinates
    private final static String SAVEDLOC = "savedloc"; // its NAME of country and city
    private final static String SAVEDTIME = "savedtime";
    private final static long DEFTIMEOFDELAY = 3600000;

    public final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static int GPS_ENABLER_REQUEST_CODE = 2;
    public final static int REQUEST_CHECK_SETTINGS = 3;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

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
            getCoordinatesWithLoader(fix, getLastLocation(), update);
        } else {
            cfinterface.stoprefreashing();
            mActivity.toastmaker(context.getResources().getString(R.string.stoprefresh));
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

    private void getCoordinatesWithLoader(String fix, String latandlng, boolean update) {

        Bundle coordinatesbundle = new Bundle();
        coordinatesbundle.putString(COORDINATES_IN_BUNDLE, fix);
        coordinatesbundle.putString(LOCATION_IN_BUNDLE, latandlng);
        if ((update) || (isLoaderExist)) {
            mLoader.restartLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isupdate = true;
        } else {
            mLoader.initLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isLoaderExist = true;
        }
    }

    //return StartLocation NAME on first app lounch, then return loc NAME from prefs
    private String getLastLocation() {

        if (getFromPrefs(PREFSNAME, true) == null) {
            return new StringBuilder().append(defoultLAT).append(",").append(defoultLNG).toString();
        } else {
            return getFromPrefs(PREFSNAME, true).replace("\"", "");
        }
    }

    //if "doweeneedlocation" is TRUE returns SAVEDSTRING means coordinates LAT and LNG, if FALSE method returns SAVEDLOC means last asked location NAME
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
        } else {
            mLoader.initLoader(R.id.past_loader_id, Bundle.EMPTY, this);
        }
    }

    //start intent to autocompletion location
    void startActivity(Context context) {

//        try {
//            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                    .build(mActivity);
//
//            centralFragment.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//        } catch (GooglePlayServicesRepairableException e) {
//        } catch (GooglePlayServicesNotAvailableException e) {
//        }

        if (!Places.isInitialized()) {
            Places.initialize(context, Keys.getGooglePlacesPrivateKey());
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(mActivity);

        mActivity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

    }

    //trying to get coordinates from gps
    void getGpsPermission(CentralFragmentImpl centralFragment) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //check if GPS is ON, make intent to ON if its OFF

        if (locationManager != null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                mActivity.toastmaker(context.getString(R.string.pleaseenablegps));
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                centralFragment.startActivityForResult(intent, GPS_ENABLER_REQUEST_CODE);

            } else {

                if (Build.VERSION.SDK_INT >= 23) {

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getCoordinatesFromGps();
                    } else {
                        cfinterface.getGpsPermission();
                    }
                } else {
                    getCoordinatesFromGps();
                }
            }
        } else {
            informUserAboutGpsUnavaliable();
        }
    }

    void gerCoordinatesDirectlyFromGps () {


    }

    //here try to get coordinates from GPS
    void getCoordinatesFromGps() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(DEFTIMEOFDELAY);
        mLocationRequest.setFastestInterval(DEFTIMEOFDELAY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);

        //we're actually already check the permissions up here, but AS thought its mistake, so we check it again
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cfinterface.getGpsPermission();
        }

        mLocationCallback = new LocationCallback()   {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (locationResult!=null) {
                onLocationChanged(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
            } else {
                informUserAboutGpsUnavaliable();
            }
        }};


        // new Google API SDK v11 uses getFusedLocationProviderClient
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                null);

        task.addOnFailureListener(mActivity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ResolvableApiException) {
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(mActivity,
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error.
                                }
                            }
                        }
                    });

    }

    //HERE IS METHOD WHEN GPS COORDINATION ACSESSED
    private void onLocationChanged(Double latitude, Double longitude) {
        //first check if gps returns null then show to user gps problem toast
        if (latitude != null && longitude !=null) {

            //protect network from unnecessary requwest - check if user already asking same location, if he's not - lounch NEW loader with coordinates from GPS
            if (isDataObsoled(GPS_LOC+": "+String.valueOf(latitude)+","+String.valueOf(longitude))) {
                String coordinatesfromgpstostring = String.valueOf(latitude)+","+String.valueOf(longitude);
                getCoordinatesWithLoader(GPS_LOC, coordinatesfromgpstostring, false);
            } else {
                cfinterface.stoprefreashing();
                mActivity.toastmaker(context.getResources().getString(R.string.stoprefresh));
            }
        } else {
            informUserAboutGpsUnavaliable();
        }
    }

    void informUserAboutGpsUnavaliable() {
        mActivity.toastmaker(context.getString(R.string.nogps));
    }

    private void adapterrefresh(List<Datum> mylist, boolean isdatanew, boolean remooveelements) {

        if (remooveelements) {

            for (int i = 0; i < 5; i++) {
                mylist.remove(0);
            }
        }
        cfinterface.adapterrefresh(mylist);
        cfinterface.stoprefreashing();
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
            } else
            {
                //if internet on phone is lost close progress and show toast in CentralFragmentImpl adapterrefresh
                adapterrefresh(null, true, false);
            }
        }
        if (id == R.id.past_loader_id){
            if (data != null && data.size()>8){
                List<Datum> datawithbite = new PrognosticModel(data).createBiteList();
                if (datawithbite != null){
                    addListToSharedPrefs(datawithbite);
                    adapterrefresh(datawithbite, true, true);
                } else {
                    //if internet on phone is lost close progress and show toast in CentralFragmentImpl adapterrefresh
                    adapterrefresh(null, true, false);
                }
            } else {
                //if internet on phone is lost close progress and show toast in CentralFragmentImpl adapterrefresh
                adapterrefresh(null, true, false);
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


