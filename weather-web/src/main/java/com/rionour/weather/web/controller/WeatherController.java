package com.rionour.weather.web.controller;

import com.google.common.collect.Lists;
import com.rionour.weather.web.model.Weather;
import com.rionour.weather.web.model.WeatherCrawl;
import com.rionour.weather.web.model.WeatherCrawlRepository;
import com.rionour.weather.web.model.WeatherStore;
import com.rionour.weather.web.model.WeatherStoreRepository;
import com.rionour.weather.web.service.WeatherService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    WeatherService weatherService;
    @Autowired
    WeatherStoreRepository weatherStoreRepository;
    @Autowired
    WeatherCrawlRepository weatherCrawlRepository;


    @ResponseBody
    @RequestMapping("/crawl")
    public void crawl() {
        weatherService.crawl();
    }

    @ResponseBody
    @RequestMapping("/refresh")
    public void refresh() {
        weatherService.store();

    }


    @ResponseBody
    @RequestMapping("/city/{code}/{day}")
    public List<Weather> weather(@PathVariable("code") String code,
                                 @PathVariable("day") String day,
                                 @RequestParam(defaultValue = "false") boolean temp) {
        String cityCode = (String) weatherService.getCityMap().get(code);
        if (cityCode != null) {
            code = cityCode;
        }
        try {
            Date date = DateUtils.parseDate(day, "yyyy-MM-dd");
            if (temp) {
                return weatherCrawlRepository.findByCodeAndDayOrderByHour(code, date).stream().map(it -> {
                    return (Weather) it;
                }).collect(Collectors.toList());
            } else {
                return weatherStoreRepository.findByCodeAndDayOrderByHour(code, date).stream().map(it -> {
                    return (Weather) it;
                }).collect(Collectors.toList());
            }

        } catch (ParseException e) {
            return Lists.newArrayList();
        }

    }

}
