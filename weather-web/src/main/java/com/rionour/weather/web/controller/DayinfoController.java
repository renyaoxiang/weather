package com.rionour.weather.web.controller;

import com.rionour.weather.web.model.DayinfoStore;
import com.rionour.weather.web.model.DayinfoStoreRepository;
import com.rionour.weather.web.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("dayinfo")
public class DayinfoController {

    @Autowired
    DayinfoStoreRepository dayinfoStoreRepository;

    @Autowired
    WeatherService weatherService;

    @ResponseBody
    @RequestMapping("/city/{code}")
    public Iterable<DayinfoStore> weather(@PathVariable("code") String code) {
        String cityCode = (String) weatherService.getCityMap().get(code);
        if (cityCode != null) {
            code = cityCode;
        }
        return dayinfoStoreRepository.findByCodeOrderByDayDesc(code);
    }

}
