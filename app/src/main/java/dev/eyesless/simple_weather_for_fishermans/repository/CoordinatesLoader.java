package dev.eyesless.simple_weather_for_fishermans.repository;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;

import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.fragments.CentralFragmentPresenter;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import retrofit2.Call;

public class CoordinatesLoader extends AsyncTaskLoader<Location> {

    private String coordinates;
    private String private_key;

    public CoordinatesLoader(Context context, Bundle args) {
        super(context);

        this.coordinates = args.getString(CentralFragmentPresenter.COORDINATES_IN_BUNDLE);
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
        Log.e("MY_TAG", "create coordinates loader");

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Location loadInBackground() {
        Call<Geocod> response = geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(coordinates, private_key);
        Location incomelocation = null;
        try {
            incomelocation = response.execute().body().getResults().get(0).getGeometry().getLocation();
            return incomelocation;
        } catch (IOException e) {
            Log.e("MY_TAG", e.getMessage());
        }
        return null;
    }
}
