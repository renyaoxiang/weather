package com.rionour.weather.web.controller;

import com.google.common.collect.Lists;
import com.rionour.weather.web.model.*;
import com.rionour.weather.web.service.WeatherService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/city")
@CrossOrigin(value = "*")
public class CityController {

    @Autowired
    WeatherService weatherService;
    @Autowired
    WeatherStoreRepository weatherStoreRepository;
    @Autowired
    WeatherCrawlRepository weatherCrawlRepository;

    @Autowired
    CrawlerCityRepository crawlerCityRepository;

    @RequestMapping("/list")
    public Iterable<CrawlerCity> list() {
        return crawlerCityRepository.findAll();
    }

    @RequestMapping("/remove")
    public void remove(@RequestBody CrawlerCity city) {
        crawlerCityRepository.deleteById(city.getId());
    }

    @RequestMapping("/update")
    public CrawlerCity update(@RequestBody CrawlerCity _city) {

        CrawlerCity city = crawlerCityRepository.findById(_city.getId()).orElse(null);
        if (city == null) {
            throw new RuntimeException("code or name is exist");
        } else {
            city.setState(_city.isState());
            return crawlerCityRepository.save(city);
        }
    }

    @RequestMapping("/create")
    public CrawlerCity create(@RequestBody CrawlerCity _city) {

        CrawlerCity city = crawlerCityRepository.findByCodeOrName(_city.getCode(), _city.getName());
        if (city == null) {
            city = _city;
            return crawlerCityRepository.save(city);
        } else {
            throw new RuntimeException("code or name is exist");
        }

    }


}
