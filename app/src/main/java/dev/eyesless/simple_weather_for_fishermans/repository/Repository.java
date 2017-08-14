package dev.eyesless.simple_weather_for_fishermans.repository;

import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.api_interface.geocoding_interfaces;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private final Double DEFAULT_LAT = 56.009657;
    private final Double DEFAULT_LNG = 37.9456611;
    private final String DEFAULT_LOCATION = "Москва, Россия";

    private Repository_interface repository_interface;
    private String private_key;
    
    private Daily tempreturneddaily;

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
                        Log.e("MY_TAG", "get last location");
                    }
                }
                catch (Exception e) {
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

    //create set of weather data and callback it to presenter setForecastDataSet
    public void getWeatherDataset() {

        tempreturneddaily = new Daily();

        Datum monday = new Datum();
        monday.setTime(1502658000);
        monday.setTemperatureMin(17.56);
        monday.setTemperatureMax(23.96);
        monday.setPressure(1000.00);
        monday.setWindBearing(215);
        monday.setPrecipProbability(0.47);

        Datum tusday = new Datum();
        tusday.setTime(1502744400);
        tusday.setTemperatureMin(18.06);
        tusday.setTemperatureMax(25.90);
        tusday.setPressure(1100.00);
        tusday.setWindBearing(120);
        tusday.setPrecipProbability(1);

        Datum wensday = new Datum();
        wensday.setTime(1502830800);
        wensday.setTemperatureMin(14.36);
        wensday.setTemperatureMax(21.06);
        wensday.setPressure(1200.00);
        wensday.setWindBearing(10);
        wensday.setPrecipProbability(0);

        List<Datum> listdatum = new ArrayList<>();

        listdatum.add(monday);
        listdatum.add(tusday);
        listdatum.add(wensday);

        tempreturneddaily.setData(listdatum);
        tempreturneddaily.setSummary("погода ацтой");
        
        repository_interface.setForecastdataset(tempreturneddaily);
    }
}
