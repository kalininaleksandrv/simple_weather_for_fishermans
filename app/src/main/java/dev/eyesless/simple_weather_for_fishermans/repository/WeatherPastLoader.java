package dev.eyesless.simple_weather_for_fishermans.repository;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.api_interface.weather_interface;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Weather;
import retrofit2.Call;
import retrofit2.Response;

public class WeatherPastLoader extends AsyncTaskLoader <List<Datum>> {

    private String private_key_weather;
    private String coordinates;
    private Long currenttime;
    private  List<Datum> listofdata;

    private final String EXCLUDE = "hourly";
    private final String LANG = "ru";
    private final String UNITS = "si";

    private final int PAST_DEPTH = 4;

    private final long ONE_DAY_IN_MS = 86400;

    private boolean isDataDelivery = false;



    public WeatherPastLoader(Context context, List<Datum> listofdata) {
        super(context);

        this.private_key_weather = dev.eyesless.simple_weather_for_fishermans.Keys.getDarkSkyPrivateKey();
        this.listofdata = listofdata;
        this.coordinates = listofdata.get(0).getCustomccordinates();
        this.currenttime = listofdata.get(0).getTime();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (!isDataDelivery){
        forceLoad();
        }
    }

    @Override
    public List<Datum> loadInBackground() {

        for (int i=0; i<PAST_DEPTH; i++){
            long nextcurrenttime = currenttime - ONE_DAY_IN_MS;
            String askedtime = coordinates +","+ String.valueOf(nextcurrenttime);

            Call<Weather> weather_response = weather_interface.WeatherFactory.getInstance().getWeatherForecasts(private_key_weather,
                    askedtime, EXCLUDE, LANG, UNITS);

            try {
                Response<Weather> resp = weather_response.execute();

                if (resp.isSuccessful()){
                        Datum nextdatum = resp.body().getDaily().getData().get(0);
                        listofdata.add(0, nextdatum);
                        setCurrenttime(nextcurrenttime);}

            } catch (IOException e) {
                return null;}
            }

        isDataDelivery = true;
        return listofdata;
    }

    private void setCurrenttime(Long currenttime) {
        this.currenttime = currenttime;
    }
}
