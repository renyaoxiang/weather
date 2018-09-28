package com.rionour.weather.web.share.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WeatherData {
    @JsonProperty("od0")
    String time;
    @JsonProperty("od1")
    String city;
    @JsonProperty("od2")
    List<HourWeatherData> houreDataList;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<HourWeatherData> getHoureDataList() {
        return houreDataList;
    }

    public void setHoureDataList(List<HourWeatherData> houreDataList) {
        this.houreDataList = houreDataList;
    }
}