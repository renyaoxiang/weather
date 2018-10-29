package com.rionour.weather.web.service.support;

import com.rionour.weather.web.model.CrawlerCityRepository;
import com.rionour.weather.web.model.DayinfoCrawlRepository;
import com.rionour.weather.web.model.WeatherCrawlRepository;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PreDestroy;

@Component
public class MyWebCrawlerFactory implements CrawlController.WebCrawlerFactory {

    private WeatherCrawlRepository repository;
    private PlatformTransactionManager txManager;
    private DayinfoCrawlRepository dayinfoRepository;
    CrawlerCityRepository crawlerCityRepository;

    public MyWebCrawlerFactory(@Autowired WeatherCrawlRepository repository,
                               @Autowired CrawlerCityRepository crawlerCityRepository,
                               @Autowired DayinfoCrawlRepository dayinfoRepository,
                               @Autowired PlatformTransactionManager txManager) {
        this.repository = repository;
        this.txManager = txManager;
        this.dayinfoRepository = dayinfoRepository;
        this.crawlerCityRepository=crawlerCityRepository;
    }

    @Override
    public WebCrawler newInstance() {
        return new MyCrawler(crawlerCityRepository, repository, dayinfoRepository, txManager);
    }

}
