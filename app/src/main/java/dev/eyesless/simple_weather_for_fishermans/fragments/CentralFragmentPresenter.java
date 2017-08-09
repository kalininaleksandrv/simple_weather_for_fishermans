package dev.eyesless.simple_weather_for_fishermans.fragments;

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
    private final String DEFOULT_LOC = "Москва, Россия";
    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String autocompleted;


    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();

    }

     void isBtnPressed() {

         autocompleted = cfinterface.getautocompleetedresult();

         String fix;

         if (autocompleted == null){fix = DEFOULT_LOC.replaceAll("\\s+","+");}
         else
             {fix = autocompleted.replaceAll("\\s+","+");}

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

    void isImgBtnPressed() {


        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(mActivity);
            mActivity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Failed ", e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Failed ", e.getMessage());        }

    }

    void setActivity(AMainActivity aMainActivity) {

        this.mActivity = aMainActivity;

    }

    String getDefoultLoc() {
        return DEFOULT_LOC;
    }



}
