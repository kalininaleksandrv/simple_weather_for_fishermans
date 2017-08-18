
package dev.eyesless.simple_weather_for_fishermans.weather_response_classes;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Flags implements Serializable
{

    @SerializedName("sources")
    @Expose
    private List<String> sources = null;
    @SerializedName("isd-stations")
    @Expose
    private List<String> isdStations = null;
    @SerializedName("units")
    @Expose
    private String units;
    private final static long serialVersionUID = -7732592651114712946L;

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getIsdStations() {
        return isdStations;
    }

    public void setIsdStations(List<String> isdStations) {
        this.isdStations = isdStations;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

}
