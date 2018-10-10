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

@Component
public class WeatherService {

    @Autowired
    CrawlController.WebCrawlerFactory factory;

    @Autowired
    WeatherCrawlRepository weatherCrawlRepository;

    @Autowired
    WeatherStoreRepository weatherStoreRepository;

    private Map<String, String> cityMap = Maps.newHashMap();

    private List<String> codeList = Lists.newArrayList();

    public Map<String, String> getCityMap() {
        return cityMap;
    }

    public List<String> getCodeList() {
        return codeList;
    }


    @Scheduled(cron = "0 0 1 * * *")
    @Async
    public void startSchedule() throws Exception {
        this.crawl(codeList);
    }

    @Scheduled(cron = "0 0 5 * * *")
    @Async
    public void storeSchedule() throws Exception {
        this.store();
    }

    @Async
    public void store() {
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

    @Async
    public void crawl() {
        try {
            this.crawl(this.codeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        InputStream content = (InputStream) new URL("http://www.baidu.com").openConnection().getContent();
        String contentString = StreamUtils.copyToString(content, Charset.defaultCharset());
        IOUtils.closeQuietly(content);
        System.out.println(contentString);
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
            controller.addSeed("http://www.weather.com.cn/weather/" + code + ".shtml");
        });
        controller.start(factory, numberOfCrawlers);
    }

    @PostConstruct
    public void init() {


        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("city.properties"), "UTF-8")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String key : properties.stringPropertyNames()) {
            String value = (String) properties.getProperty(key);
            cityMap.put(key, value);
            codeList.add(value);
        }
    }
}
