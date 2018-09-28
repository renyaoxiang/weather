package com.rionour.weather.web.model;

import javax.persistence.*;

@Entity
public class CrawlerCity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;
    @Column(unique = true)
    String code;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
