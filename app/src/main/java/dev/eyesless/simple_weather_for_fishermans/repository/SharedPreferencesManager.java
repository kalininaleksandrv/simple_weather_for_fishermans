package dev.eyesless.simple_weather_for_fishermans.repository;

interface SharedPreferencesManager {
    void addToPrefs (String value, String value2);
    String getFromPrefs (String prefname);
    void remoovePrefs (String prefname);
}
