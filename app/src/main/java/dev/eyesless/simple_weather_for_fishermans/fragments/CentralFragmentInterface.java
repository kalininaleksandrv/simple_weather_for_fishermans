package dev.eyesless.simple_weather_for_fishermans.fragments;


import android.location.Location;

import com.google.android.gms.tasks.Task;

import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

interface CentralFragmentInterface {

    //call when presenter set a coord in view
    void setCoords(String s);

    void setDefoultLoc();

    void setLocUnavaliable();

    void adapterrefresh(List<Datum> mylist);

    void stoprefreashing();

    void getGpsPermission();
}

