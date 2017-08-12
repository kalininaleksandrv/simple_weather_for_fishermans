
package dev.eyesless.simple_weather_for_fishermans.geocoding_responce_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;

    public String getLastlocation() {
        return lastlocation;
    }

    public void setLastlocation(String lastlocation) {
        this.lastlocation = lastlocation;
    }

    private String lastlocation;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
