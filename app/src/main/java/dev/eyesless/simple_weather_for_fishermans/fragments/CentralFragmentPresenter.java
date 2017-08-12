package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import dev.eyesless.simple_weather_for_fishermans.repository.Repository;
import dev.eyesless.simple_weather_for_fishermans.repository.Repository_interface;

class CentralFragmentPresenter implements Repository_interface {

    private CentralFragmentInterface cfinterface;
    private Repository repository;
    private AMainActivity mActivity;
    private final String DEFOULT_LOC = "Москва, Россия";
    private String autocompleted;
    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;


        repository = new Repository (this);
    }

    //call when button pressed in IMPL
     void startSearch() {

         String fix;

         //prepare to request autocompleeted place or defoult plase (Moscow, Russia)
         if (autocompleted == null){fix = DEFOULT_LOC.replaceAll("\\s+","+");}
         else
             {fix = autocompleted.replaceAll("\\s+","+");}

         repository.getCoordsByLocation (fix);


    }

    // set aMainActivity
    void setActivity(AMainActivity aMainActivity) {
        this.mActivity = aMainActivity;
    }

    //get autocompleeted or default value of location
    String getDefoultLoc() {
        if (autocompleted == null){return DEFOULT_LOC;}
        else {
            return autocompleted;
        }
    }

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

    void setAutocompleted(String autocompleted) {
        this.autocompleted = autocompleted;
    }

    @Override
    public void setCoordinates(Location location) {

        cfinterface.setCoords(String.valueOf(location.getLat()) + " and " + String.valueOf(location.getLng()));

        if (location.getLastlocation() != null)
        setAutocompleted(location.getLastlocation());
        cfinterface.setDefoultLoc();
        cfinterface.setLocUnavaliable();

    }
}
