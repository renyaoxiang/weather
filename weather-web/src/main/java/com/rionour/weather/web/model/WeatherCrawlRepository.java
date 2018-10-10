package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public interface WeatherCrawlRepository extends PagingAndSortingRepository<WeatherCrawl, String> {
    Iterable<WeatherCrawl> findByCodeAndDayOrderByHour(String code, Date day);
}
