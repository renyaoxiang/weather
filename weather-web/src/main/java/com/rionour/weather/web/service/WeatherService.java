package com.rionour.weather.web.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rionour.weather.web.model.*;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public class WeatherService {

    @Autowired
    CrawlController.WebCrawlerFactory factory;

    @Autowired
    WeatherCrawlRepository weatherCrawlRepository;

    @Autowired
    WeatherStoreRepository weatherStoreRepository;

    @Autowired
    CrawlerCityRepository crawlerCityRepository;

    @Autowired
    DayinfoCrawlRepository dayinfoCrawlRepository;
    @Autowired
    DayinfoStoreRepository dayinfoStoreRepository;

    private Map<String, String> cityMap = Maps.newHashMap();


    public Map<String, String> getCityMap() {
        return cityMap;
    }

    public List<String> getCodeList() {
        return crawlerCityRepository
                .findAllByState(true)
                .stream()
                .map(it -> it.getCode())
                .collect(Collectors.toList());
    }


    @Scheduled(cron = "0 0 1 * * *")
    @Async
    public void startSchedule() throws Exception {
        this.crawl(this.getCodeList());
    }

    @Scheduled(cron = "0 0 5 * * *")
    @Async
    public void storeSchedule() throws Exception {
        this.store();
    }


    @Async
    public void store() {
        this.storeDayinfo();
        this.storeWeather();

    }

    private void storeWeather() {
        Iterable<WeatherCrawl> weatherCrawls = weatherCrawlRepository.findAll();
        weatherCrawls.forEach(it -> {
            WeatherStore weatherStore = weatherStoreRepository.findByCodeAndDayAndHour(it.getCode(), it.getDay(), it.getHour());
            if (weatherStore == null) {
                weatherStore = new WeatherStore();
                BeanUtils.copyProperties(it, weatherStore);
                weatherStoreRepository.save(weatherStore);
            }
        });
        weatherCrawlRepository.deleteAll(weatherCrawls);

    }

    private void storeDayinfo() {

        Iterable<DayinfoCrawl> dayinfoCrawls = dayinfoCrawlRepository.findAll();
        dayinfoCrawls.forEach(it -> {
            DayinfoStore dayinfoStore = dayinfoStoreRepository.findByCodeAndDay(it.getCode(), it.getDay());
            if (dayinfoStore == null) {
                dayinfoStore = new DayinfoStore();
                BeanUtils.copyProperties(it, dayinfoStore);
                dayinfoStoreRepository.save(dayinfoStore);
            }
        });
        dayinfoCrawlRepository.deleteAll();
    }

    @Async
    public void crawl() {
        try {
            this.crawl(this.getCodeList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void crawl(List<String> codeList) throws Exception {
        if (codeList.size() == 0) {
            return;
        }
        String crawlStorageFolder = Files.createTempDirectory("weather").toAbsolutePath().toString();
        int numberOfCrawlers = 10;
        CrawlConfig config = new CrawlConfig();
        config.setThreadShutdownDelaySeconds(3000);
        config.setCleanupDelaySeconds(3000);
        config.setCrawlStorageFolder(crawlStorageFolder);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        codeList.stream().forEach(code -> {
            controller.addSeed("http://www.weather.com.cn/weather1d/" + code + ".shtml");
        });
        controller.start(factory, numberOfCrawlers);
    }

    @PostConstruct
    @Transactional
    public void init() {


        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("city.properties"), "UTF-8")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String name : properties.stringPropertyNames()) {
            String code = properties.getProperty(name);
            CrawlerCity crawlerCity = crawlerCityRepository.findByCode(code);
            if (crawlerCity == null) {
                crawlerCityRepository.save(new CrawlerCity(code, name));
            }
        }
    }
}
