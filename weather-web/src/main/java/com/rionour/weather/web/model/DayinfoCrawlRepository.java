package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DayinfoCrawlRepository extends PagingAndSortingRepository<DayinfoCrawl, String> {

    List<DayinfoCrawl> findByCodeOrderByDayDesc(String code);
}

