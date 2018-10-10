package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public interface WeatherStoreRepository extends PagingAndSortingRepository<WeatherStore, String> {

    WeatherStore findByCodeAndDayAndHour(String code, Date day, String hour);

    Iterable<WeatherStore> findByCodeAndDayOrderByHour(String code, Date day);
}
