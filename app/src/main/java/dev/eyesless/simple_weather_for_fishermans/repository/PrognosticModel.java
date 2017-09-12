package dev.eyesless.simple_weather_for_fishermans.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public class PrognosticModel {

    final static int PROG_TEMPRETURE_MAX = 22;
    final static int PROG_TEMPRETURE_MIN = -5;

    private List<Datum> incomedata;

    private List<Datum> outcomedata;

    public PrognosticModel(List<Datum> incomedata) {
        this.incomedata = incomedata;
    }

    public List<Datum> createBiteList(){
            if (incomedata != null) {
                outcomedata = new ArrayList<>();
                Iterator<Datum> in_iterator = incomedata.iterator();
                while (in_iterator.hasNext()) {
                    Datum defoultdatum = in_iterator.next();
                    defoultdatum.setIsBite(biteforecast(defoultdatum));
                    getOutcomedata().add(defoultdatum);
                }
                Log.e("MY_TAG", "return NEW outcomedata");
                return outcomedata;

            } else {
                Log.e("MY_TAG", "incoming data is NULL");
                return null;
            }
    }

    private String biteforecast(Datum defoultdatum) {

        if ((defoultdatum.getTemperatureMax()>PROG_TEMPRETURE_MAX)||(defoultdatum.getTemperatureMin()<PROG_TEMPRETURE_MIN)){
            return "bad";
        } else {
            return "good";}
    }

    public List<Datum> getOutcomedata() {
        return outcomedata;
    }

    public void setOutcomedata(List<Datum> outcomedata) {
        this.outcomedata = outcomedata;
    }
}
