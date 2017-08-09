package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CentralFragmentPresenter {

    private CentralFragmentInterface cfinterface;
    private String private_key;
    private AMainActivity mActivity;
    private Context context;
    private final String DEFOULT_LOC = "Москва, Россия";
    private String autocompleted;


    CentralFragmentPresenter(CentralFragmentInterface cfi, Context context) {

        this.cfinterface = cfi;
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
        this.context = context;

    }

    //call when button pressed in IMPL
     void isBtnPressed() {

         //get new adress from autocompleet callback
         autocompleted = cfinterface.getautocompleetedresult();

         String fix;

         //prepare to request autocompleeted place or defoult plase (Moscow, Russia)
         if (autocompleted == null){fix = DEFOULT_LOC.replaceAll("\\s+","+");}
         else
             {fix = autocompleted.replaceAll("\\s+","+");}

         //send request to google maps and return result to setCoords method of Implementation (which set it up to textView)
         geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(fix, private_key).enqueue(new Callback<Geocod>() {
             @Override
             public void onResponse(@NonNull Call<Geocod> call, @NonNull Response<Geocod> response) {
                 double lat = 0;
                 double lng = 0;
                 try {
                     lat = response.body().getResults().get(0).getGeometry().getLocation().getLat();
                     lng = response.body().getResults().get(0).getGeometry().getLocation().getLng();
                 } catch (Exception e) {
                     Log.e("MY_TAG", e.getMessage());
                 }
                 cfinterface.setCoords(String.valueOf(lat) + " - " + String.valueOf(lng));
             }
             @Override
             public void onFailure(@NonNull Call<Geocod> call, @NonNull Throwable t) {
                 Log.e("Failed ", t.getMessage());
             }
         });
    }
    // start autocompleet intent on mainactivity level
    void isImgBtnPressed() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(mActivity);
            mActivity.startActivityForResult(intent, AMainActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Failed ", e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Failed ", e.getMessage());        }

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

}
