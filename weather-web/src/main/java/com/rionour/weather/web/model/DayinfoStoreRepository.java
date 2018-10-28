package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public interface DayinfoStoreRepository extends PagingAndSortingRepository<DayinfoStore, String> {

    DayinfoStore findByCodeAndDay(String code, Date day);

    Iterable<DayinfoStore> findByCodeOrderByDayDesc(String code);
}

