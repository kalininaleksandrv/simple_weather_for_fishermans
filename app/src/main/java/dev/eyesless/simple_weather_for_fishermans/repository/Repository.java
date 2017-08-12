package dev.eyesless.simple_weather_for_fishermans.repository;

import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.util.Log;

import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private final Double DEFAULT_LAT = 56.009657;
    private final Double DEFAULT_LNG = 37.9456611;
    private final String DEFAULT_LOCATION = "Москва, Россия";

    private Repository_interface repository_interface;
    private String private_key;

    public Repository(Repository_interface repository_interface) {
        this.repository_interface = repository_interface;
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
    }

    public void getCoordsByLocation(final String fix) {

        //send request to google maps and return result to setCoords method of Implementation (which set it up to textView)
        geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(fix, private_key).enqueue(new Callback<Geocod>() {
            @Override
            public void onResponse(@NonNull Call<Geocod> call, @NonNull Response<Geocod> response) {
                Location incomelocation = null;
                try {
                    if (response.body().getResults() != null){
                    incomelocation = response.body().getResults().get(0).getGeometry().getLocation();}
                    else {
                        incomelocation = getLastLocation();
                    }
                }
                catch (Exception e) {
                    repository_interface.setCoordinates(getLastLocation());
                    Log.e("MY_TAG", e.getMessage());
                }
                repository_interface.setCoordinates(incomelocation);
            }
            @Override
            public void onFailure(@NonNull Call<Geocod> call, @NonNull Throwable t) {
                Log.e("Failed ", t.getMessage());
                repository_interface.setCoordinates(getLastLocation());
            }
        });
    }

    private Location getLastLocation() {
        Location lastlocation = new Location();
        lastlocation.setLat(DEFAULT_LAT);
        lastlocation.setLng(DEFAULT_LNG);
        lastlocation.setLastlocation(DEFAULT_LOCATION);
        return lastlocation;
    }
}
