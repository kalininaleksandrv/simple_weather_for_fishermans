package dev.eyesless.simple_weather_for_fishermans.repository;

import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.fragments.RVadapter;
import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public interface Repository_interface {

    void setCoordinates(Location location);

    void adapterrefresh(List<Datum> mylist, boolean isdatanew);
}
