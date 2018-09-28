package com.rionour.weather.web.service.support;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import org.springframework.stereotype.Component;

@Component
public class MyWebCrawlerFactory implements CrawlController.WebCrawlerFactory {
    WebCrawler crawler;

    public MyWebCrawlerFactory(WebCrawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public WebCrawler newInstance() throws Exception {
        return crawler;
    }
}
