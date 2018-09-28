package com.rionour.weather.web.share.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class HourWeatherData {

    @JsonProperty("od21")
    String hour;
    @JsonProperty("od22")
    String od22;
    @JsonProperty("od23")
    String od23;
    @JsonProperty("od24")
    String od24;
    @JsonProperty("od25")
    String od25;
    @JsonProperty("od26")
    String od26;
    @JsonProperty("od27")
    String od27;
    @JsonProperty("od28")
    String od28;

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getOd22() {
        return od22;
    }

    public void setOd22(String od22) {
        this.od22 = od22;
    }

    public String getOd23() {
        return od23;
    }

    public void setOd23(String od23) {
        this.od23 = od23;
    }

    public String getOd24() {
        return od24;
    }

    public void setOd24(String od24) {
        this.od24 = od24;
    }

    public String getOd25() {
        return od25;
    }

    public void setOd25(String od25) {
        this.od25 = od25;
    }

    public String getOd26() {
        return od26;
    }

    public void setOd26(String od26) {
        this.od26 = od26;
    }

    public String getOd27() {
        return od27;
    }

    public void setOd27(String od27) {
        this.od27 = od27;
    }

    public String getOd28() {
        return od28;
    }

    public void setOd28(String od28) {
        this.od28 = od28;
    }
}