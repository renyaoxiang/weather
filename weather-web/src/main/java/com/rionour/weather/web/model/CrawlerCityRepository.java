package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CrawlerCityRepository extends PagingAndSortingRepository<CrawlerCity, String> {
    List<CrawlerCity> findAllByState(boolean state);

    CrawlerCity findByCode(String code);

    CrawlerCity findByCodeOrName(String code, String name);
}
