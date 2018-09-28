package com.rionour.weather.web.service;

import com.google.common.collect.Lists;
import com.rionour.weather.web.model.CrawlerCityRepository;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WeatherService {

    @Autowired
    CrawlController.WebCrawlerFactory factory;

    @Autowired
    CrawlerCityRepository repository;


    @Scheduled(cron = "0 30 * * * *")
    @Async
    public void start() throws Exception {
        List<String> codeList = Lists.newArrayList(repository.findAll()).stream().map(it -> it.getCode()).collect(Collectors.toList());
        this.start(codeList);
    }

    public void start(List<String> codeList) throws Exception {
        if (codeList.size() == 0) {
            return;
        }
        String crawlStorageFolder = Files.createTempDirectory("weather").toAbsolutePath().toString();
        int numberOfCrawlers = Math.max(5, codeList.size());
        CrawlConfig config = new CrawlConfig();
        config.setThreadShutdownDelaySeconds(3);
        config.setCleanupDelaySeconds(3);
        config.setCrawlStorageFolder(crawlStorageFolder);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        codeList.stream().forEach(code -> {
            controller.addSeed("http://www.weather.com.cn/weather/" + code + ".shtml");
        });
        controller.start(factory, numberOfCrawlers);
        controller.waitUntilFinish();
        System.out.println("finish");

    }
}
