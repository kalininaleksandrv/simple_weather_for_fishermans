package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;



public class RVadapter extends RecyclerView.Adapter<RVadapter.WeatherViewHolder>{

    private List<Datum> weatherdataset;

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        CardView cardviewweather;
        TextView temperature;
        TextView wind;
        TextView pressure;
        TextView percipe;
        TextView date;

        WeatherViewHolder(View itemView) {
            super(itemView);

            cardviewweather = (CardView)itemView.findViewById(R.id.card_view_weather);
            temperature = (TextView) itemView.findViewById(R.id.textView_tempr);
            wind = (TextView) itemView.findViewById(R.id.textView_wind);
            pressure = (TextView) itemView.findViewById(R.id.textView_pressure);
            percipe = (TextView) itemView.findViewById(R.id.textView_percip);
            date = (TextView) itemView.findViewById(R.id.textView_data);
        }
    }

    RVadapter(List<Datum> data) {

        this.weatherdataset = data;
        Log.e("MY_TAG", "creating RV " + data.get(0).getSummary());


    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new WeatherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {

        holder.temperature.setText(String.valueOf((int)weatherdataset.get(position).getTemperatureMin())+(char) 0x00B0+ "C / " +
        String.valueOf((int)weatherdataset.get(position).getTemperatureMax()) +(char) 0x00B0+ "C");
        holder.wind.setText(String.format("ветер - %s", windfrom(weatherdataset.get(position).getWindBearing())));
        holder.pressure.setText("давление - " + String.valueOf((int)weatherdataset.get(position).getPressure())+"мм. рт. ст.");
        holder.percipe.setText(String.valueOf((int)(weatherdataset.get(position).getPrecipProbability()*100)+" %"));
        holder.date.setText(dateconverter(weatherdataset.get(position).getTime()));

    }

    @Override
    public int getItemCount() {
        return weatherdataset.size();
    }

    //convert direction of wind from degree to string
       private String windfrom(long windBearing) {
           String direction = "??";
           if (windBearing > 30 && windBearing < 61) {direction = "северо-восточный";}
           if (windBearing > 60 && windBearing < 121) {direction = "восточный";}
           if (windBearing > 120 && windBearing < 151) {direction = "юго-восточный";}
           if (windBearing > 150 && windBearing < 211) {direction = "южный";}
           if (windBearing > 210 && windBearing < 241) {direction = "юго-западный";}
           if (windBearing > 240 && windBearing < 301) {direction = "западный";}
           if (windBearing > 300 && windBearing < 331) {direction = "северо-западный";}
           if ((windBearing > 330 && windBearing < 361) || (windBearing > 0 && windBearing < 31) ) {direction = "северный";}
           return direction;
       }

    //convert unix time to string
    private String dateconverter(long time) {

        if (time != 0) {
            Date date = new Date(time*1000L);

            SimpleDateFormat sdf = new SimpleDateFormat("EE, dd-MM-yyyy");
            // TODO: 14.08.2017 disable this warning

            return sdf.format(date);
        } else {

            return "00-00-0000";
        }

    }
}
