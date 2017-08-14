package dev.eyesless.simple_weather_for_fishermans.repository;

import dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes.Location;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Daily;

public interface Repository_interface {

    void setCoordinates(Location location);

    void setForecastdataset(Daily forecastdataset);


//    Daily getForecast (String coordinates);
//
//    void setForecast (Daily daily);
//
//    Daily getPast (String coordinates);
//
//    void setPast (Daily daily);

}
