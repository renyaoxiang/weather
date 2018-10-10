package com.rionour.weather.web.share.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class HourWeatherData {

    /**
     * 小时
     */
    @JsonProperty("od21")
    String od21;
    /**
     * 温度
     */
    @JsonProperty("od22")
    String od22;
    /**
     * unknow
     */
    @JsonProperty("od23")
    String od23;
    /**
     * 风向
     */
    @JsonProperty("od24")
    String od24;
    /**
     * 风力
     */
    @JsonProperty("od25")
    String od25;
    /**
     * 降水
     */
    @JsonProperty("od26")
    String od26;
    /**
     * 相对湿度
     */
    @JsonProperty("od27")
    String od27;
    /**
     * 空气质量
     */
    @JsonProperty("od28")
    String od28;

    public String getOd21() {
        return od21;
    }

    public void setOd21(String od21) {
        this.od21 = od21;
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