package dev.eyesless.simple_weather_for_fishermans.api_interface;

import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Geocod;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface geocoding_interfaces {

    String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    String PRIVATE_KEY = "";


    OkHttpClient CLIENT = new OkHttpClient();

    @GET("json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key="+PRIVATE_KEY)
    Call<Geocod> getCoordinates();

    class CoordinatesFactory {

        private static geocoding_interfaces service;

        public static geocoding_interfaces getInstance() {

            if (service == null) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(CLIENT)
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
