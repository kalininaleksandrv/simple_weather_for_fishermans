package dev.eyesless.simple_weather_for_fishermans.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

import static java.lang.Math.max;

public class PrognosticModel {


    private final static int PROG_TEMPRETURE_MAX_OPTIMUM = 18;
    private final static int PROG_TEMPRETURE_MIN_OPTIMUM = -7;
    private final static int PROG_TEMPRETURE_MAX_WORSE = 24;
    private final static int PROG_TEMPRETURE_MIN_WORSE = -14;
    private final static int PROG_TEMPRETURE_MAX_BAD = 30;
    private final static int PROG_TEMPRETURE_MIN_BAD = -22;

    private final static String WEATHER_GOOD = "good";
    private final static String WEATHER_AVERAGE = "average";
    private final static String WEATHER_DOWNWARD = "downward";
    private final static String WEATHER_BAD = "bad";
    private final static String WEATHER_NO_DATA = "nodata";


    private final static int PROGNOSE_DEPTH = 5;
    private final static int DAYS_COUNT = 7;

    private final List<Datum> incomedata;
    private List<Datum> outcomedata;

    private final double[] maxtemparray;
    private final double[] mintemparray;
    private final static double PRESSUREDEVIATION = 0.008; //officially changing pressure more than 8% defined as "sharp change"

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

        int pressureAccumulatedEffect = 0;

        for (int j = 0; j < DAYS_COUNT; j++) {

            int optimumcounter = 0;
            int worsecounter = 0;
            int badcounter = 0;
            int disastercounter = 0;

            int [] temperatureArray = new int[5];
            double [] pressureArray = new double[5];
            double [] precipArray = new double[5];


            //filling arrays by data
            for (int i = 0; i < PROGNOSE_DEPTH; i++) {

                temperatureArray[i] = isTempretureGood(incomedata.get(startingday+i).getTemperatureMin(), incomedata.get(startingday+i).getTemperatureMax());
                pressureArray [i] = incomedata.get(startingday+i).getPressure();
                precipArray[i] = incomedata.get(startingday+i).getPrecipIntensity();
            }

            //effect of temperature
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

            //effect of fast normalising of temperature
            if (temperatureArray[4]<=1){

                int temp = temperatureArray[4];

                if ((temperatureArray[3]-temp)>=2 && (temperatureArray[2]-temp)>=2 && (temperatureArray[1]-temp)>=2 && (temperatureArray[0]-temp)>=2){

                    optimumcounter++;
                    Log.i("MY_TAG", "Effect of fast normalising: last day = " + String.valueOf(startingday) + "previous days = " + String.valueOf(temperatureArray[3])+ String.valueOf(temperatureArray[2])+ String.valueOf(temperatureArray[1])+ String.valueOf(temperatureArray[0]));

                }
            }

            //effect of fast pressure change
            int isPressureDeviant = 0;

            //here we're count how many times difference between today and yesterday pressure was >X% and increase var. isDeviantPressure, X = PRESSUREDEVIATION
            for (int i = 4; i>0; i--){
                double currentPressure = pressureArray[i];
                double currentPressureDeviation = currentPressure * PRESSUREDEVIATION;
                double previouspressure = pressureArray [i-1];

                if (Math.abs(currentPressure-previouspressure)>currentPressureDeviation){
                    isPressureDeviant++;
                }

            }
            // here we're check - how many times pressure was deviant strongly and decreasing forecast depend of that
            switch (isPressureDeviant) {
                case 0 :
                    pressureAccumulatedEffect--; //accumulated effect using in final count (look below) - if pressure effect was bad 3 days in a row, so we inc and dec this war each cycle
                    break;
                case 1 :
                    pressureAccumulatedEffect--;
                    break;
                case 2 :
                    optimumcounter--;
                    worsecounter++;
                    Log.i("MY_TAG", "Pressure WORSE");
                    break;
                case 3 :
                    optimumcounter-=2;
                    worsecounter ++;
                    badcounter ++;
                    pressureAccumulatedEffect++;
                    Log.i("MY_TAG", "Pressure BAD");
                    break;
                case 4 :
                    optimumcounter-=3;
                    worsecounter ++;
                    badcounter+=3;
                    disastercounter++;
                    pressureAccumulatedEffect++;
                    Log.i("MY_TAG", "Pressure DISASTER");
                    break;
            }

            // TODO: 28.01.2018 precip intensity
            // 0.5-1 liitle rain part day, 1-2 rain part day little rain all day, 1.5-2.5 rain all day, 2,5-4 heavy rain all day its mm per hour so 1mm = 24 mm/day,
            // officialy havy rain - 30-100 mm/day, wery havy rain - 100-200 mm/day
            //should programmed effects of 1 - "firs day rain" 2 - "third day rain" 3 - "first day dry after long rain"


            Log.i("MY_TAG", "Starting day is: " + String.valueOf(startingday));
            Log.i("MY_TAG", "Array is: " + Arrays.toString(temperatureArray));
            Log.i("MY_TAG", "Counter is" + " disaster = " + String.valueOf(disastercounter)
                    + " bad = " + String.valueOf(badcounter)
                    + " worse = " + String.valueOf(worsecounter)
                    + " optimum = " + String.valueOf(optimumcounter));

            if (pressureAccumulatedEffect<3) {
                if ( max(optimumcounter, worsecounter) > max(badcounter, disastercounter)){

                    if (optimumcounter > worsecounter){
                        dayEstimate[j]=WEATHER_GOOD;
                    } else {
                        dayEstimate[j]=WEATHER_AVERAGE;
                    }
                } else if (badcounter > disastercounter) {
                    dayEstimate[j]=WEATHER_DOWNWARD;
                } else {
                    dayEstimate[j]=WEATHER_BAD;
                }
            } else {
                //if pressure changing fast 3 and more days bite will be bad despite other effects
                if (pressureAccumulatedEffect>=3){
                        dayEstimate[j]="bad";
                        Log.i("MY_TAG", "Pressure ACCUM BAD");
                }
            }

            //increasing starting day (j)
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
