package dev.eyesless.simple_weather_for_fishermans.api_interface;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import dev.eyesless.simple_weather_for_fishermans.BuildConfig;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Weather;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface weather_interface {

    String BASE_URL = "https://api.darksky.net/";

    @GET("forecast/{key}/{coords}")
    Call<Weather> getWeatherForecasts (@Path("key") String key,
                                       @Path("coords") String coords,
                                       @Query("exclude") String exclude,
                                       @Query("lang") String lang,
                                       @Query("units") String units);


    class WeatherFactory {

        private static weather_interface service;

        public static weather_interface getInstance() {

            if (service == null) {

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override public void log(@NonNull String message) {
                        Log.e("MY_TAG", "OkHttp: " + message);
                    }
                });

                logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(logging) // TODO: 15.10.2017 remoove before production
                        .retryOnConnectionFailure(false)
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(1, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                service = retrofit.create(weather_interface.class);

                return service;
            }
            else {
                return service;
            }
        }
    }
}


