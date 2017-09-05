package dev.eyesless.simple_weather_for_fishermans.repository;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.api_interface.weather_interface;
import dev.eyesless.simple_weather_for_fishermans.fragments.CentralFragmentPresenter;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Weather;
import retrofit2.Call;


public class WeatherLoader extends AsyncTaskLoader <List<Datum>> {

    private String private_key_weather;
    private String coordinates;
    private String private_key;


    private final String EXCLUDE = "hourly";
    private final String LANG = "ru";
    private final String UNITS = "si";

    private final Double DEFAULT_LAT = 56.009657;
    private final Double DEFAULT_LNG = 37.9456611;
    private final String DEFAULT_LOCATION = "Москва, Россия";


    public WeatherLoader(Context context, Bundle args) {
        super(context);

        this.private_key_weather = dev.eyesless.simple_weather_for_fishermans.Keys.getDarkSkyPrivateKey();
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
        this.coordinates = args.getString(CentralFragmentPresenter.COORDINATES_IN_BUNDLE);
        Log.e("MY_TAG", "create coordinates loader");

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Datum> loadInBackground() {


        Call<Geocod> response = geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(coordinates, private_key);
        Location incomelocation = getLastLocation();
        try {
            incomelocation = response.execute().body().getResults().get(0).getGeometry().getLocation();
            Log.e("MY_TAG", "try to request new loc");
        } catch (IOException e) {
            Log.e("MY_TAG", e.getMessage());
        }

        Call<Weather> weather_response = weather_interface.WeatherFactory.getInstance().getWeatherForecasts(private_key_weather,
                from_loc_to_string(incomelocation), EXCLUDE, LANG, UNITS);
        List<Datum> mylist;

            try {
                mylist = weather_response.execute().body().getDaily().getData();
                    if (mylist.size()!=0){
                    mylist.remove(0);
                    Log.e("MY_TAG", "weather reqwes body is SUCSESS");
                    return mylist;
                    }
                else {
                        Log.e("MY_TAG", "weather reqwes body is NULL");
                        return getastrvadapterlist();
                    }
            } catch (IOException e) {
                Log.e("MY_TAG", e.getMessage());
                return getastrvadapterlist();
            }

    }

    private List<Datum> getastrvadapterlist() {

        Datum defoultdatum = new Datum();
        defoultdatum.setNew(false);
        List<Datum> mydatum = new ArrayList<>();
        mydatum.add(defoultdatum);

        return mydatum;
    }

    private Location getLastLocation() {
        Log.e("MY_TAG", "Setting Lastlocation");
        Location lastlocation = new Location();
        lastlocation.setLat(DEFAULT_LAT);
        lastlocation.setLng(DEFAULT_LNG);
        lastlocation.setLastlocation(DEFAULT_LOCATION);
        return lastlocation;
    }

    private String from_loc_to_string(Location incomeLocation) {
        return String.valueOf(incomeLocation.getLat())+","+String.valueOf(incomeLocation.getLng());
    }

}
