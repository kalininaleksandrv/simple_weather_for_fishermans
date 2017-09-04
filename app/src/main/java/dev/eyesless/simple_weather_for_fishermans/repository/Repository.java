package dev.eyesless.simple_weather_for_fishermans.repository;

import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.fragments.RVadapter;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import dev.eyesless.simple_weather_for_fishermans.api_interface.weather_interface;

public class Repository {

    private final Double DEFAULT_LAT = 56.009657;
    private final Double DEFAULT_LNG = 37.9456611;
    private final String DEFAULT_LOCATION = "Москва, Россия";

    private final String EXCLUDE = "hourly";
    private final String LANG = "ru";
    private final String UNITS = "si";

    private Repository_interface repository_interface;
    private String private_key;
    private String private_key_weather;
    private Location lastlocation;


    public Repository(Repository_interface repository_interface) {
        this.repository_interface = repository_interface;
        this.private_key = dev.eyesless.simple_weather_for_fishermans.Keys.getGoogleMapPrivateKey();
        this.private_key_weather = dev.eyesless.simple_weather_for_fishermans.Keys.getDarkSkyPrivateKey();
    }

    public void getCoordsByLocation(final String fix) {

        //send request to google maps and return result to setCoords method of Implementation (which set it up to textView)
        geocoding_interfaces.CoordinatesFactory.getInstance().getCoordinates(fix, private_key).enqueue(new Callback<Geocod>() {
            @Override
            public void onResponse(@NonNull Call<Geocod> call, @NonNull Response<Geocod> response) {
                Location incomelocation = null;
                if (response.body().getResults() != null) {
                    try {
                        incomelocation = response.body().getResults().get(0).getGeometry().getLocation();
                    } catch (Exception e) {
                        Log.e("MY_TAG", e.getMessage());
                    }
                } else {
                    setIncomelocation(getLastLocation());
                    Log.e("MY_TAG", "COORDS reqwest OK but prodused NULL: ");
                }
                setIncomelocation(incomelocation);
                repository_interface.setCoordinates(getIncomeLocation());
                getWeatherDataset(getIncomeLocation());
            }
            @Override
            public void onFailure(@NonNull Call<Geocod> call, @NonNull Throwable t) {
                Log.e("Failed ", t.getMessage());
                setIncomelocation(getLastLocation());
                repository_interface.setCoordinates(getIncomeLocation());
            }
        });
    }

    //create set of weather data and callback it to presenter setForecastDataSet
    public void getWeatherDataset(Location location) {

        String coordsstringbilder;

        if (location!=null) {

            coordsstringbilder = from_loc_to_string (location);

        } else {

            coordsstringbilder = from_loc_to_string (getLastLocation());
        }

        weather_interface.WeatherFactory.getInstance().getWeatherForecasts(private_key_weather,
                coordsstringbilder, EXCLUDE, LANG, UNITS).enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {

                if (response.body().getDaily() != null) {
                    List<Datum> mylist;
                    mylist = response.body().getDaily().getData();
                    mylist.remove(0);
                    repository_interface.adapterrefresh(mylist, true);
                } else {
                    repository_interface.adapterrefresh(getastrvadapterlist(), false);
                    Log.e("MY_TAG", "WEATHER reqwest OK but prodused NULL: ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {
                repository_interface.adapterrefresh(getastrvadapterlist(), false);
                Log.e("MY_TAG", "reqwest FAILURE: " + t.toString());
            }
        });
    }

    private String from_loc_to_string(Location incomeLocation) {

        return String.valueOf(incomeLocation.getLat())+","+String.valueOf(incomeLocation.getLng());
    }

    private Location getIncomeLocation() {
        return lastlocation;
    }

    private void setIncomelocation(Location lastlocation) {
        this.lastlocation = lastlocation;
    }

    public List<Datum> getastrvadapterlist() {

        Datum defoultdatum = new Datum();
        List<Datum> mydatum = new ArrayList<>();
        mydatum.add(defoultdatum);

        return mydatum;
    }

    public Location getLastLocation() {
        Log.e("MY_TAG", "Setting Lastlocation");
        Location lastlocation = new Location();
        lastlocation.setLat(DEFAULT_LAT);
        lastlocation.setLng(DEFAULT_LNG);
        lastlocation.setLastlocation(DEFAULT_LOCATION);
        return lastlocation;
    }
}

// TODO: 17.08.2017 data must cashed in differend thread, shoul develop shem of cashing - first cash or View and so on 
// TODO: 17.08.2017 show and hide statusbars when cashing 