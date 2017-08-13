package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;



class RVadapter extends RecyclerView.Adapter{
    private List<Datum> weatherdataset;

    RVadapter(List<Datum> data) {

        this.weatherdataset = data;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
