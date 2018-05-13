package dev.eyesless.simple_weather_for_fishermans.api_interface;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import dev.eyesless.simple_weather_for_fishermans.BuildConfig;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface geocoding_interfaces {

    String BASE_URL = "https://maps.googleapis.com/maps/api/";

    @GET("geocode/json")
    Call<Geocod> getCoordinates(@Query("address") String order, @Query("key") String key);

    class CoordinatesFactory {

        private static geocoding_interfaces service;

        public static geocoding_interfaces getInstance() {

            if (service == null) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(false)
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(1, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                service = retrofit.create(geocoding_interfaces.class);

                return service;
            }

            else {
                return service;
            }
        }
    }
}
