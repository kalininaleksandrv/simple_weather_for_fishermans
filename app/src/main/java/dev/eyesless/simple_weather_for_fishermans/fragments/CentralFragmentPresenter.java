package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Result;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CentralFragmentPresenter {

    private CentralFragmentInterface cfinterface;
    private String private_key;


    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();

    }

     void isBtnPressed() {

        String a = cfinterface.getPlace();

        String fix = a.replaceAll("\\s+","+");

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

}
