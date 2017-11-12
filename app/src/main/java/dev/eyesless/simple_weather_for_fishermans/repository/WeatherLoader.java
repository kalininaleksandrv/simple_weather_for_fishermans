package dev.eyesless.simple_weather_for_fishermans.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;

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
import retrofit2.Response;


public class WeatherLoader extends AsyncTaskLoader <List<Datum>> {

    private String private_key_weather;
    private String coordinates;
    private String locations;
    private String private_key;

    private final String EXCLUDE = "hourly";
    private final String LANG = "ru";
    private final String UNITS = "si";

    private String defoult_city = "Москва, Россия";

    private List<Datum> mylist;



    public WeatherLoader(Context context, Bundle args) {
        super(context);

        this.private_key_weather = dev.eyesless.simple_weather_for_fishermans.Keys.getDarkSkyPrivateKey();
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
        this.coordinates = args.getString(CentralFragmentPresenter.COORDINATES_IN_BUNDLE);
        this.locations = args.getString(CentralFragmentPresenter.LOCATION_IN_BUNDLE);
        Log.e("MY_TAG", "create coordinates loader");

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mylist==null){
            forceLoad();
        }
    }

    @Override
    public List<Datum> loadInBackground() {

        String askinglocation;
        Location incomelocation;

        if (!coordinates.equals("First,+Lounch")){
            Call<Geocod> response = geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(coordinates, private_key);
            try {
                Log.e("MY_TAG", "try to request new loc " + coordinates);
                incomelocation = response.execute().body().getResults().get(0).getGeometry().getLocation();
                askinglocation = from_loc_to_string(incomelocation);
            } catch (IOException e) {
                Log.e("MY_TAG", "coordinates request FAIL  "+ e.getMessage());
                askinglocation = locations;
            }
        } else {
                coordinates = defoult_city;
                Log.e("MY_TAG", "try to request loc from getLastLocation ");
                askinglocation = locations;
        }

        Call<Weather> weather_response = weather_interface.WeatherFactory.getInstance().getWeatherForecasts(private_key_weather,
                askinglocation, EXCLUDE, LANG, UNITS);

            try {

                Response<Weather> resp = weather_response.execute();
                if (resp.isSuccessful()){
                    mylist = resp.body().getDaily().getData();
                    mylist.get(0).setCustomccordinates(askinglocation);
                    mylist.get(0).setCustomlocationname(coordinates.replace("+", " "));
                    Log.e("MY_TAG", "weather request body is SUCSESS " + "coords is: " + askinglocation + "place is: " + coordinates.replace("+", " "));
                    return mylist;
                    }
                else {
                    int statusCode = resp.code();
                        Log.e("MY_TAG", "weather request: server return unexpected code: " + String.valueOf(statusCode));
                        return null;
                    }
            } catch (IOException e) {
                Log.e("MY_TAG", "weather request FAIL  "+ e.getMessage());
                return null;
            }

    }

    private String from_loc_to_string(Location incomeLocation) {
        return String.valueOf(incomeLocation.getLat())+","+String.valueOf(incomeLocation.getLng());
    }

}
