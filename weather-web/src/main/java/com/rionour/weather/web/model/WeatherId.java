package com.rionour.weather.web.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WeatherId implements Serializable {

    String code;

    String hour;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherId weatherId = (WeatherId) o;
        return Objects.equals(code, weatherId.code) &&
                Objects.equals(hour, weatherId.hour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, hour);
    }
}


