package dev.eyesless.simple_weather_for_fishermans.fragments;


 interface CentralFragmentInterface {

     //call when presenter set a coord in view
     void setCoords (String s);

     void startActivityFromPresenter ();

     void setDefoultLoc();

     void setLocUnavaliable ();


 }
