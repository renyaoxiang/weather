package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public interface WeatherRepository extends PagingAndSortingRepository<Weather, WeatherId> {
}
