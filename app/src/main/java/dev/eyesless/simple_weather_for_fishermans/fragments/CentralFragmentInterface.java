package dev.eyesless.simple_weather_for_fishermans.fragments;


import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

interface CentralFragmentInterface {

     //call when presenter set a coord in view
     void setCoords (String s);

     void startActivityFromPresenter ();

     void setDefoultLoc();

     void setLocUnavaliable ();

     void adapterrefresh(List<Datum> mylist, boolean isdatanew);

 }
