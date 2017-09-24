package dev.eyesless.simple_weather_for_fishermans.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

public class PrognosticModel {


    private final static int PROG_TEMPRETURE_MAX_OPTIMUM = 23;
    private final static int PROG_TEMPRETURE_MIN_OPTIMUM = -4;
    private final static int PROG_TEMPRETURE_MAX_WORSE = 29;
    private final static int PROG_TEMPRETURE_MIN_WORSE = -9;
    private final static int PROG_TEMPRETURE_MAX_BAD = 36;
    private final static int PROG_TEMPRETURE_MIN_BAD = -19;

    private final static int DEPTH_OF_FORECAST = 5;

    private final static double DAY0_COEFF = 0.05;
    private final static double DAY1_COEFF = 0.1;
    private final static double DAY2_COEFF = 0.2;
    private final static double DAY3_COEFF = 0.25;
    private final static double DAY4_COEFF = 0.4;

    private final List<Datum> incomedata;
    private List<Datum> outcomedata;

    private final double[] maxtemparray = new double[13];
    private final double[] mintemparray = new double[13];

    public PrognosticModel(List<Datum> incomedata) {
        this.incomedata = incomedata;
    }

    public List<Datum> createBiteList(){
            if (incomedata != null) {
                outcomedata = new ArrayList<>();
                modeldatamaker();
                Iterator<Datum> in_iterator = incomedata.iterator();
                int count = 0;
                while (in_iterator.hasNext()) {
                    Datum defoultdatum = in_iterator.next();
                    defoultdatum.setIsBite(biteforecast(defoultdatum, count));
                    count ++;
                    getOutcomedata().add(defoultdatum);
                }
                Log.w("MY_TAG", "return NEW outcomedata");
                return outcomedata;

            } else {
                Log.w("MY_TAG", "incoming data is NULL");
                return null;
            }
    }

    private void modeldatamaker (){

        Iterator<Datum> iterator = incomedata.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Datum defoultdatum = iterator.next();
            maxtemparray[count] = defoultdatum.getTemperatureMax();
            mintemparray[count] = defoultdatum.getTemperatureMin();
            count ++;
        }

    }

    private String biteforecast(Datum defoultdatum, int count) {
        if (count > DEPTH_OF_FORECAST-1){
            double bitescore = 0;
            String weather = "unknown";
            for (int i = 0; i<DEPTH_OF_FORECAST; i++){
                switch (i) {
                    case 0: bitescore = bitescore+(isweathergood (mintemparray[count-5], maxtemparray[count-5])*DAY0_COEFF); break;
                    case 1: bitescore = bitescore+(isweathergood (mintemparray[count-4], maxtemparray[count-4])*DAY1_COEFF); break;
                    case 2: bitescore = bitescore+(isweathergood (mintemparray[count-3], maxtemparray[count-3])*DAY2_COEFF); break;
                    case 3: bitescore = bitescore+(isweathergood (mintemparray[count-2], maxtemparray[count-2])*DAY3_COEFF); break;
                    case 4: bitescore = bitescore+(isweathergood (mintemparray[count-1], maxtemparray[count-1])*DAY4_COEFF); break;
                }
            }

            if (bitescore >= 100) {
                Log.w("MY_TAG", "ret GOOD for day " + count + " " + bitescore);
                weather = "good";
            }
            if ((bitescore>89)&&(bitescore<100)) {
                Log.w("MY_TAG", "ret AVERAGE for day " + count + " " + bitescore);
                weather =  "average";

            }
            if ((bitescore>74)&&(bitescore<90)) {
                Log.w("MY_TAG", "ret DOWNWARD for day " + count + " " + bitescore);
                weather =  "downward";
            }
            if (bitescore <75) {
                Log.w("MY_TAG", "ret BAD for day " + count + " " + bitescore);
                weather =  "bad";
            }
            Log.w("MY_TAG", "ret WEATHER " + weather + " " + bitescore);
            return weather;

        } else {
            if ((defoultdatum.getTemperatureMax() > PROG_TEMPRETURE_MAX_OPTIMUM) || (defoultdatum.getTemperatureMin() < PROG_TEMPRETURE_MIN_OPTIMUM)) {
                return "bad";
            } else {
                return "good";
            }
        }
    }

    private int isweathergood(double mintemp, double maxtemp) {
        double averagetemp = (mintemp+maxtemp)/2;
        int isweathercounter = 0;
        if (averagetemp<0){

            if (mintemp>=PROG_TEMPRETURE_MIN_OPTIMUM){
                Log.w("MY_TAG", "ret 100");
                isweathercounter = isweathercounter+100;
            }
            if (mintemp<PROG_TEMPRETURE_MIN_OPTIMUM && mintemp>=PROG_TEMPRETURE_MIN_WORSE){
                Log.w("MY_TAG", "ret 90");
                isweathercounter = isweathercounter+90;
            }
            if (mintemp<PROG_TEMPRETURE_MIN_WORSE && mintemp>=PROG_TEMPRETURE_MIN_BAD){
                Log.w("MY_TAG", "ret 75");
                isweathercounter = isweathercounter+75;
            }
            if (mintemp<PROG_TEMPRETURE_MIN_BAD){
                Log.w("MY_TAG", "ret 50");
                isweathercounter = isweathercounter+50;
            }
        } else {
            if (maxtemp<=PROG_TEMPRETURE_MAX_OPTIMUM){
                Log.w("MY_TAG", "ret 100");
                isweathercounter = isweathercounter+100;
            }
            if (maxtemp>PROG_TEMPRETURE_MAX_OPTIMUM && maxtemp<=PROG_TEMPRETURE_MAX_WORSE){
                Log.w("MY_TAG", "ret 90");
                isweathercounter = isweathercounter+90;
            }
            if (maxtemp>PROG_TEMPRETURE_MAX_WORSE && maxtemp<=PROG_TEMPRETURE_MAX_BAD){
                Log.w("MY_TAG", "ret 75");
                isweathercounter = isweathercounter+75;
            }
            if (maxtemp>PROG_TEMPRETURE_MAX_BAD){
                Log.w("MY_TAG", "ret 50");
                isweathercounter = isweathercounter+50;
            }
        }
        return isweathercounter;
    }

    private List<Datum> getOutcomedata() {
        return outcomedata;
    }
    public void setOutcomedata(List<Datum> outcomedata) {
        this.outcomedata = outcomedata;
    }
}
