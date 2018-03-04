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
    private final static int PROG_IS_WARM_PART = 7;

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

    //in this method we provide incoming list of Datum to outcoming.
    public List<Datum> createBiteList(){
            if (incomedata != null) {
                outcomedata = new ArrayList<>();

                Iterator<Datum> in_iterator = incomedata.iterator();
                int count = 0;

                String[] arrayOfPrognose;

                boolean isccordrange = isCordinatesInRange(incomedata);

                if (isccordrange) {
                    arrayOfPrognose = getIncomeData();
                } else {
                    arrayOfPrognose = getNoData();
                }

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

    //check coordinates belong to range
    private boolean isCordinatesInRange(List<Datum> incomedata){

           //there is String with coordinates, we put in incomedata in WeatherLoader
            String incomestring = incomedata.get(4).getCustomccordinates();

        if (incomestring!=null) {
            //spliting one string to walues and convert it to double
            String [] splittingstring = incomestring.split(",", 2);
            Double latitude = Double.valueOf(splittingstring[0]);
            Double longitude = Double.valueOf(splittingstring [1]);
            Log.i("MY_TAG", "Custom coordinates = " + incomestring);

            //checking if lant and lng in range of acceptable values
            if (longitude<=-55 && longitude>=-130 ){
                return latitude >= 38 && latitude <= 62;
            } else {

                if (longitude<=21 && longitude>=-10 ){
                    return latitude >= 42 && latitude <= 60;
                } else {

                    if (longitude<=50 && longitude>22 ){
                        return latitude >= 43 && latitude <= 65;
                    } else {

                        if (longitude<=62 && longitude>51 ){
                            return latitude >= 51 && latitude <= 67;
                        } else {

                            if (longitude<=81 && longitude>63 ){
                                return latitude >= 53 && latitude <= 66;
                            } else {

                                if (longitude<=113 && longitude>82 ){
                                    return latitude >= 49 && latitude <= 60;
                                } else {

                                    return false;

                                }
                            }
                        }
                    }
                }
            }
        } else {
            return true;
        }
    }

    //if coordinates NOT belong to range fill array NODATA value
    private  String  [] getNoData (){
        String  [] dayEstimate = new String [DAYS_COUNT];
        for (int j = 0; j < DAYS_COUNT; j++) {
            dayEstimate[j] = WEATHER_NO_DATA;
        }
        return dayEstimate;
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

            boolean isWarmPartOfYear = isWarmPartOfYear(incomedata.get(0).getTemperatureMin(), incomedata.get(0).getTemperatureMax());

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

            // 0.5-1 liitle rain part day, 1-2 rain part day little rain all day, 1.5-2.5 rain all day, 2,5-4 heavy rain all day its mm per hour so 1mm = 24 mm/day,
            // officialy havy rain - 30-100 mm/day, wery havy rain - 100-200 mm/day

            //first day rain after long summer dry or first day dry after long rains
            if (isWarmPartOfYear){ //working only in warm part of year

                Log.i("MY_TAG", "PERCIP CURRENT: " + String.valueOf(precipArray[4]));
                Log.i("MY_TAG", "PERCIP ARRAY: " + String.valueOf(precipArray[3]) + " "+ String.valueOf(precipArray[2]) + " "+ String.valueOf(precipArray[1]) + " ");

                double precipcompareindex = (Math.abs((precipArray[3]+precipArray[2]+precipArray[1])/3)-precipArray[4]); //difference between current day and average value of 3 previous days

                if (precipArray[3]>0.5 && precipArray[2]>0.5 && precipArray[1]>0.5){

                    Log.i("MY_TAG", "LONG RAINS CONDITION");

                    optimumcounter--;
                    worsecounter ++;
                    badcounter++;
                }

                if(precipArray[4]<0.5 && precipcompareindex>1){ //found sharpness of precipitation intensity

                    Log.i("MY_TAG", "FIRST DAY precipitation CHANGING CONDITION");

                    optimumcounter+=3;
                    worsecounter --;
                    badcounter=0;
                    disastercounter=0;
                }
            }



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

    private boolean isWarmPartOfYear(double temperatureMin, double temperatureMax) {

        double incometemperature = (temperatureMin+temperatureMax)/2;


        return incometemperature > PROG_IS_WARM_PART;
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
