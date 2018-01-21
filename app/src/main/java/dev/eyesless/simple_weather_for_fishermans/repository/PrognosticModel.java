package dev.eyesless.simple_weather_for_fishermans.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

import static java.lang.Math.max;

public class PrognosticModel {


    private final static int PROG_TEMPRETURE_MAX_OPTIMUM = 23;
    private final static int PROG_TEMPRETURE_MIN_OPTIMUM = -4;
    private final static int PROG_TEMPRETURE_MAX_WORSE = 29;
    private final static int PROG_TEMPRETURE_MIN_WORSE = -9;
    private final static int PROG_TEMPRETURE_MAX_BAD = 36;
    private final static int PROG_TEMPRETURE_MIN_BAD = -19;

    private final static int PROGNOSE_DEPTH = 5;
    private final static int DAYS_COUNT = 7;

    private final List<Datum> incomedata;
    private List<Datum> outcomedata;

    private final double[] maxtemparray;
    private final double[] mintemparray;

    public PrognosticModel(List<Datum> incomedata) {
        this.incomedata = incomedata;
        maxtemparray = new double[13];
        mintemparray = new double[13];
    }

    //in this method wee provide incoming list od Datum to outcoming.
    public List<Datum> createBiteList(){
            if (incomedata != null) {
                outcomedata = new ArrayList<>();

                Iterator<Datum> in_iterator = incomedata.iterator();
                int count = 0;

                String [] arrayOfPrognose = getIncomeData();

                while (in_iterator.hasNext()) {
                    Datum defoultdatum = in_iterator.next();
                    //skip 5 steps then start to write data from array
                    if (count<PROGNOSE_DEPTH) {

                        defoultdatum.setIsBite("no_data"); //point of adding bite forecast
                    }else {

                        defoultdatum.setIsBite(arrayOfPrognose[count-5]);
                    }
                    count ++;
                    getOutcomedata().add(defoultdatum);
                }
                return outcomedata;

            } else {
                return null;
            }
    }

    private String [] getIncomeData() {

        int startingday = 0;

        String  [] dayEstimate = new String [DAYS_COUNT];

        for (int j = 0; j < DAYS_COUNT; j++) {

            int optimumcounter = 0;
            int worsecounter = 0;
            int badcounter = 0;
            int disastercounter = 0;

            int [] temperatureArray = new int[5];


            for (int i = 0; i < PROGNOSE_DEPTH; i++) {

                temperatureArray[i] = isTempretureGood(incomedata.get(startingday+i).getTemperatureMin(), incomedata.get(startingday+i).getTemperatureMax());

            }
            for (int i=0; i<5; i++){

                if (Math.abs(temperatureArray[i]) == 3){
                    disastercounter++;
                }if (Math.abs(temperatureArray[i]) == 2){
                    badcounter++;
                }
                if (Math.abs(temperatureArray[i]) == 1){
                    worsecounter++;
                }
                if (Math.abs(temperatureArray[i]) == 0){
                    optimumcounter++;
                }
            }


            Log.i("MY_TAG", "Starting day is: " + String.valueOf(startingday));
            Log.i("MY_TAG", "Array is: " + Arrays.toString(temperatureArray));
            Log.i("MY_TAG", "Counter is" + " disaster = " + String.valueOf(disastercounter)
                    + " bad = " + String.valueOf(badcounter)
                    + " worse = " + String.valueOf(worsecounter)
                    + " optimum = " + String.valueOf(optimumcounter));

            if ( max(optimumcounter, worsecounter) > max(badcounter, disastercounter)){

                if (optimumcounter > worsecounter){
                    dayEstimate[j]="good";
                } else {
                    dayEstimate[j]="average";
                }
            } else if (badcounter > disastercounter) {
                dayEstimate[j]="downward";
            } else {
                dayEstimate[j]="bad";
            }

            startingday++;

        }

        Log.i("MY_TAG", "Days estimate array is: " + Arrays.toString(dayEstimate));

        return dayEstimate;

    }

    private int isTempretureGood(double temperatureMin, double temperatureMax){

        double incometemperature = (temperatureMin+temperatureMax)/2;

        int temperaturescore;

        if (incometemperature < PROG_TEMPRETURE_MIN_BAD){
            temperaturescore = -3;
        } else if (incometemperature>= PROG_TEMPRETURE_MIN_BAD && incometemperature <PROG_TEMPRETURE_MIN_WORSE){
            temperaturescore = -2;

        } else if (incometemperature>= PROG_TEMPRETURE_MIN_WORSE && incometemperature <PROG_TEMPRETURE_MIN_OPTIMUM){
            temperaturescore = -1;

        } else if (incometemperature>= PROG_TEMPRETURE_MIN_OPTIMUM && incometemperature <PROG_TEMPRETURE_MAX_OPTIMUM){
            temperaturescore = 0;

        } else if (incometemperature>= PROG_TEMPRETURE_MAX_OPTIMUM && incometemperature <PROG_TEMPRETURE_MAX_WORSE){
            temperaturescore = 1;

        } else if (incometemperature>= PROG_TEMPRETURE_MAX_WORSE && incometemperature <PROG_TEMPRETURE_MAX_BAD){
            temperaturescore = 2;

        }  else if (incometemperature>=PROG_TEMPRETURE_MAX_BAD){
            temperaturescore = 3;

        }  else temperaturescore = 0;

        return temperaturescore;
    }

    private List<Datum> getOutcomedata() {
        return outcomedata;
    }
}
