package com.rionour.weather.web.controller;

import com.google.common.collect.Lists;
import com.rionour.weather.web.model.WeatherCrawlRepository;
import com.rionour.weather.web.model.WeatherStore;
import com.rionour.weather.web.model.WeatherStoreRepository;
import com.rionour.weather.web.service.WeatherService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/city")
public class CityController {

    @Autowired
    WeatherService weatherService;
    @Autowired
    WeatherStoreRepository weatherStoreRepository;
    @Autowired
    WeatherCrawlRepository weatherCrawlRepository;


    @ResponseBody
    @RequestMapping("/info")
    public Map<String, String> infos() {
        return weatherService.getCityMap();
    }


    @ResponseBody
    @RequestMapping("/info/{city}")
    public String info(@PathVariable String city) {
        return weatherService.getCityMap().get(city);
    }
}
