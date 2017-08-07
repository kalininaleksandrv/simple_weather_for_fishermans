package dev.eyesless.simple_weather_for_fishermans.api_interface;

public interface weather_interface {

    String BASE_URL_WEATHER = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Moscow%2C%20RU%22)%20and%20u%3D%27c%27&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";



}


