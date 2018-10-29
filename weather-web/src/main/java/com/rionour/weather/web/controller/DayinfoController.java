package com.rionour.weather.web.controller;

import com.google.common.collect.Lists;
import com.rionour.weather.web.model.Dayinfo;
import com.rionour.weather.web.model.DayinfoCrawlRepository;
import com.rionour.weather.web.model.DayinfoStore;
import com.rionour.weather.web.model.DayinfoStoreRepository;
import com.rionour.weather.web.model.Weather;
import com.rionour.weather.web.service.WeatherService;
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
import java.util.stream.Collectors;


@Controller
@RequestMapping("dayinfo")
public class DayinfoController {

    @Autowired
    DayinfoStoreRepository dayinfoStoreRepository;

    @Autowired
    DayinfoCrawlRepository dayinfoCrawlRepository;

    @Autowired
    WeatherService weatherService;

    @ResponseBody
    @RequestMapping("/city/{code}")
    public List<Dayinfo> weather(@PathVariable("code") String code,
                                 @RequestParam(defaultValue = "false") boolean temp) {
        String cityCode = (String) weatherService.getCityMap().get(code);
        if (cityCode != null) {
            code = cityCode;
        }
        if (temp) {
            return dayinfoCrawlRepository.findByCodeOrderByDayDesc(code).stream().map(it -> {
                return (Dayinfo) it;
            }).collect(Collectors.toList());
        } else {
            return dayinfoStoreRepository.findByCodeOrderByDayDesc(code).stream().map(it -> {
                return (Dayinfo) it;
            }).collect(Collectors.toList());
        }


    }

}
