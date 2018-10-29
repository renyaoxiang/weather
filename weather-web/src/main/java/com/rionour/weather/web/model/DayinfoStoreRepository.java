package com.rionour.weather.web.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface DayinfoStoreRepository extends PagingAndSortingRepository<DayinfoStore, String> {

    DayinfoStore findByCodeAndDay(String code, Date day);

    List<DayinfoStore> findByCodeOrderByDayDesc(String code);
}

