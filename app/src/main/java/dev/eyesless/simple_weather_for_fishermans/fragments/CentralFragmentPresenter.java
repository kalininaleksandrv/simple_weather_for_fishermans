package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.repository.PrognosticModel;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherLoader;
import dev.eyesless.simple_weather_for_fishermans.repository.WeatherPastLoader;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public class CentralFragmentPresenter implements LoaderManager.LoaderCallbacks<List<Datum>> {

    private final CentralFragmentInterface cfinterface;
    private AMainActivity mActivity;
    final static String DEFOULT_LOC = "Москва, Россия";
    private String autocompleted;
    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static String COORDINATES_IN_BUNDLE = "coords";
    private LoaderManager mLoader;
    private List<Datum> midlist;
    private boolean isupdate = false;

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

        if (update) {
            mLoader.restartLoader(R.id.weather_loader_id, coordinatesbundle, this);
            isupdate = true;
            Log.e("MY_TAG", "restart loader");
        } else {
            mLoader.initLoader(R.id.weather_loader_id, coordinatesbundle, this);
            Log.e("MY_TAG", "init loader");
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
                adapterrefresh(data, isNew, false);
                getPastWithLoader(data);
                Log.e("MY_TAG", "databeforeBITE");

            } else
            {
                Log.e("MY_TAG", "geting NULL weather");
            }
        }
        if (id == R.id.past_loader_id){

            if (data != null){
                List<Datum> datawithbite = new PrognosticModel(data).createBiteList();
                if (datawithbite != null){
                    adapterrefresh(datawithbite, true, true);
                    Log.e("MY_TAG", "datawithbite");
                } else {
                    mActivity.toastmaker(mActivity.getString(R.string.nobitedata));
                    Log.e("MY_TAG", "dataNObite");
                }
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
    }

    void setAutocompleted(String autocompleted) {this.autocompleted = autocompleted;}

    String getAutocompleeted() {return autocompleted;}

    //temp Liat to init RVAdapter in starting app
    List<Datum> getTempAdapterList() {
        Datum defoultdatum = new Datum();
        List<Datum> mydatum = new ArrayList<>();
        mydatum.add(defoultdatum);
        return mydatum;
    }
}


