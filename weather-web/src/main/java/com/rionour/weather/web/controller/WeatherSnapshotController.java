package com.rionour.weather.web.controller;

import com.google.common.collect.Lists;
import com.rionour.weather.web.model.WeatherCrawl;
import com.rionour.weather.web.model.WeatherCrawlRepository;
import com.rionour.weather.web.model.WeatherStoreRepository;
import com.rionour.weather.web.service.WeatherService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

@Controller
@RequestMapping("/weatherSnapshot")
public class WeatherSnapshotController {

    @Autowired
    WeatherService weatherService;
    @Autowired
    WeatherStoreRepository weatherStoreRepository;
    @Autowired
    WeatherCrawlRepository weatherCrawlRepository;

    @ResponseBody
    @RequestMapping("/cities")
    public Iterable<WeatherCrawl> cities() {
        return weatherCrawlRepository.findAll();
    }

    @ResponseBody
    @RequestMapping("/city/{code}/{day}")
    public Iterable<WeatherCrawl> weatherShapshotCity(@PathVariable("code") String code, @PathVariable("day") String day) {

        String cityCode = (String) weatherService.getCityMap().get(code);
        if (cityCode != null) {
            code = cityCode;
        }
        try {
            return weatherCrawlRepository.findByCodeAndDayOrderByHour(code, DateUtils.parseDate(day, "yyyy-MM-dd"));
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }


}
