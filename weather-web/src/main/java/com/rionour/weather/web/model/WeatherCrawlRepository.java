package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface WeatherCrawlRepository extends PagingAndSortingRepository<WeatherCrawl, String> {
    List<WeatherCrawl> findByCodeAndDayOrderByHour(String code, Date day);
}
