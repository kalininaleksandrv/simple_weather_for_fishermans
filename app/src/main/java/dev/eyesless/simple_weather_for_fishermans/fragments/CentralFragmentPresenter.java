package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.repository.PrognosticModel;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherLoader;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherPastLoader;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public class CentralFragmentPresenter implements LoaderManager.LoaderCallbacks<List<Datum>> {

    private final CentralFragmentInterface cfinterface;
    private AMainActivity mActivity;
    final static String DEFOULT_LOC = "First, Lounch";
    private String autocompleted;
    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static String COORDINATES_IN_BUNDLE = "coords";
    private LoaderManager mLoader;
    private List<Datum> midlist;
    private boolean isupdate = false;
    private boolean isLoaderExist = false;
    private SharedPreferences sharedpref;
    private final static String DATAPREFSNAME = "dataprefsname";
    private final static String DATASAVEDSTRING = "datasavedstring";


    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;

    }

    //call when button pressed in IMPL
     void startSearch(boolean update) {

         String fix;
         //prepare to request autocompleeted place or defoult plase (Moscow, Russia)
         if (autocompleted == null){fix = DEFOULT_LOC.replaceAll("\\s+","+");}
         else
             {fix = autocompleted.replaceAll("\\s+","+");}

         getCoordinatesWithLoader (fix, update);

    }


    private void getCoordinatesWithLoader(String fix, boolean update) {

        Bundle coordinatesbundle = new Bundle();
        coordinatesbundle.putString(COORDINATES_IN_BUNDLE, fix);

        if ((update) || (isLoaderExist)) {
            mLoader.restartLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isupdate = true;
            Log.e("MY_TAG", "just restart loader");
        } else {
            mLoader.initLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isLoaderExist = true;
        }
    }

    private void getPastWithLoader (List<Datum> mylist){

          midlist = mylist;
            if (isupdate){
                mLoader.restartLoader(R.id.past_loader_id, Bundle.EMPTY, this);
                Log.e("MY_TAG", "restart PAST loader");
            }
                else {
                mLoader.initLoader(R.id.past_loader_id, Bundle.EMPTY, this);
                Log.e("MY_TAG", "init PAST loader");}
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
            Log.e("Failed: Play . n aval. ", e.getMessage());        }
    }

    private void adapterrefresh(List<Datum> mylist, boolean isdatanew, boolean remooveelements) {

        if (remooveelements) {

            for (int i = 0; i < 5; i++) {
                mylist.remove(0);
            }
        }
        cfinterface.adapterrefresh(mylist, isdatanew);
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
        Log.e("MY_TAG", "add list to shared prefs " + jsonSavedString);
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
                return new WeatherLoader(mActivity, args);
            case R.id.past_loader_id:
                return new WeatherPastLoader(mActivity, midlist);
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
                adapterrefresh(data, isNew, false);
                getPastWithLoader(data);
                Log.e("MY_TAG", "databeforeBITE " + data.size());
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
        sharedpref = mActivity.getApplicationContext().getSharedPreferences(DATAPREFSNAME, Context.MODE_PRIVATE);
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
}


