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


public class WeatherLoader extends AsyncTaskLoader <List<Datum>> implements SharedPreferencesManager {

    private String private_key_weather;
    private String coordinates;
    private String private_key;
    private Context context;


    private final String EXCLUDE = "hourly";
    private final String LANG = "ru";
    private final String UNITS = "si";

    private Double defoultLAT = 55.755826;
    private Double defoultLNG = 37.6172999;
    private String defoult_city = "Москва, Россия";

    private final static String PREFSNAME = "prefsname";
    private final static String SAVEDSTRING = "savedstr";

    private List<Datum> mylist;



    public WeatherLoader(Context context, Bundle args) {
        super(context);

        this.private_key_weather = dev.eyesless.simple_weather_for_fishermans.Keys.getDarkSkyPrivateKey();
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
        this.coordinates = args.getString(CentralFragmentPresenter.COORDINATES_IN_BUNDLE);
        this.context = context; //is this context correct?
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

        String askinglocation = "";
        Location incomelocation;

        if (!coordinates.equals("First,+Lounch")){
            defoult_city = coordinates.replace("+", " ");
            Call<Geocod> response = geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(coordinates, private_key);
            try {
                Log.e("MY_TAG", "try to request new loc " + coordinates);
                incomelocation = response.execute().body().getResults().get(0).getGeometry().getLocation();
                askinglocation = from_loc_to_string(incomelocation);
                //todo getResults may produse null
            } catch (IOException e) {
                Log.e("MY_TAG", "coordinates request FAIL  "+ e.getMessage());
            }
        } else {

                Log.e("MY_TAG", "try to request loc from getLastLocation ");
                askinglocation = getLastLocation();

        }

        Call<Weather> weather_response = weather_interface.WeatherFactory.getInstance().getWeatherForecasts(private_key_weather,
                askinglocation, EXCLUDE, LANG, UNITS);

            try {

                Response<Weather> resp = weather_response.execute();
                if (resp.isSuccessful()){
                    mylist = resp.body().getDaily().getData();
                    Log.e("MY_TAG", "weather request body is SUCSESS");
                    mylist.get(0).setCustomccordinates(askinglocation);
                    mylist.get(0).setCustomlocationname(defoult_city);
                    addToPrefs(defoult_city, askinglocation);
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

    private String getLastLocation() {

        if (getFromPrefs(PREFSNAME)==null){
            Log.e("MY_TAG", "NO PREFS, try to request loc from defoult ");
            return new StringBuilder().append(defoultLAT).append(",").append(defoultLNG).toString();
        } else {
            return getFromPrefs(PREFSNAME).replace("\"", "");
        }
    }

    private String from_loc_to_string(Location incomeLocation) {
        return String.valueOf(incomeLocation.getLat())+","+String.valueOf(incomeLocation.getLng());
    }


    @Override
    public void addToPrefs(String valuecity, String valuecoords) {
        SharedPreferences sharedpref;
        SharedPreferences.Editor editor;
        sharedpref = context.getSharedPreferences(PREFSNAME, Context.MODE_PRIVATE);

        editor = sharedpref.edit();
        Gson gson = new Gson();
        String jsonSavedString = gson.toJson(new StringBuilder().append(valuecity).append(",").append(valuecoords).toString());
        editor.putString(SAVEDSTRING, jsonSavedString);
        editor.apply();
    }

    @Override
    public String getFromPrefs(String prefname) {

        SharedPreferences sharedpref = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);

        if (sharedpref.getString(SAVEDSTRING, null) == null){
            return null;
        }
        else {
            Log.e("MY_TAG", "HERES PREFS "+sharedpref.getString(SAVEDSTRING, null));

            String [] restored = sharedpref.getString(SAVEDSTRING, null).split(","); //spliting string by sign ","

            int rlen = restored.length; //define size of array, because we're know - last two words in it is coordinates and all other is name of city country etc

            StringBuilder sb = new StringBuilder();
            for (int i=0; i<rlen-2; i++){
                sb.append(restored[i]).append(", "); //got name of city, country etc
            }

            defoult_city = sb.toString().replace("\"", "");

            return new StringBuilder().append(restored[rlen-2]).append(",").append(restored[rlen-1]).toString();//got coordinater here
        }
    }

    @Override
    public void remoovePrefs(String prefname) {

    }

}
