
package dev.eyesless.simple_weather_for_fishermans.geocoding_response_classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeocodingResponce {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
