package com.rionour.weather.web.service.support;

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

    public MyWebCrawlerFactory(@Autowired WeatherCrawlRepository repository,
                               @Autowired PlatformTransactionManager txManager) {
        this.repository = repository;
        this.txManager = txManager;
    }

    @Override
    public WebCrawler newInstance() throws Exception {
        return new MyCrawler(repository, txManager);
    }

}
