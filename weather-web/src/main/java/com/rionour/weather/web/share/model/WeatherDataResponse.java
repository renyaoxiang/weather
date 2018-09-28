package com.rionour.weather.web.share.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherDataResponse {
    @JsonProperty("od")
    WeatherData inner;

    public WeatherData getInner() {
        return inner;
    }

    public void setInner(WeatherData inner) {
        this.inner = inner;
    }
}