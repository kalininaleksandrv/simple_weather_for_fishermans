package dev.eyesless.simple_weather_for_fishermans.repository;

interface SharedPreferencesManager {
    void addToPrefs (String prefname, String value);
    String getFromPrefs (String prefname);
    void remoovePrefs (String prefname);
}
