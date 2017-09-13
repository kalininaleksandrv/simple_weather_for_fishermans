package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;



class RVadapter extends RecyclerView.Adapter<RVadapter.WeatherViewHolder>{

    private List<Datum> weatherdataset;

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        CardView cardviewweather;
        TextView temperature;
        TextView wind;
        TextView pressure;
        TextView percipe;
        TextView date;
        ImageView weather;
        ImageView fish;

        WeatherViewHolder(View itemView) {
            super(itemView);

            cardviewweather = (CardView)itemView.findViewById(R.id.card_view_weather);
            temperature = (TextView) itemView.findViewById(R.id.textView_tempr);
            wind = (TextView) itemView.findViewById(R.id.textView_wind);
            pressure = (TextView) itemView.findViewById(R.id.textView_pressure);
            percipe = (TextView) itemView.findViewById(R.id.textView_percip);
            date = (TextView) itemView.findViewById(R.id.textView_data);
            weather = (ImageView) itemView.findViewById(R.id.imageView_weather);
            fish = (ImageView) itemView.findViewById(R.id.imageView_fish);
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
        holder.weather.setImageResource(getimageresfromselector(weatherdataset.get(position).getIcon()));
        holder.fish.setImageResource(getfishresfromselector(weatherdataset.get(position).getIsBite()));
    }

    private int getfishresfromselector(String fish) {

        if (fish!=null) {
            switch (fish) {
                case "good":return R.drawable.arrowup;
                case "bad":return R.drawable.arrowdown;
                case "average":return R.drawable.arrowneutral;
                case "downward":return R.drawable.arrowneutral;

                default:
                    return R.drawable.arrowneutral;
            }
        }else {
            return R.drawable.arrowneutral;
        }
    }

    //return R.id. of weather icon based on incom string
    private int getimageresfromselector(String icon) {

        if (icon!=null) switch (icon) {
            case "partly-cloudy-day":return R.drawable.cloud_sun;
            case "partly-cloudy-night":return R.drawable.cloud_moon;
            case "clear-day":return R.drawable.sun;
            case "clear-night":return R.drawable.moon;
            case "rain":return R.drawable.cloud_rain;
            case "snow":return R.drawable.cloud_snow;
            case "sleet":return R.drawable.sleet;
            case "wind":return R.drawable.cloud_wind_sun;
            case "fog":return R.drawable.cloud_fog;
            case "cloudy":return R.drawable.cloud;

            default: return R.drawable.ic_sync_black_48dp;

        }
        else {
            return R.drawable.ic_sync_black_48dp;
        }
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
