package com.rionour.weather.web.service.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rionour.weather.web.model.Weather;
import com.rionour.weather.web.model.WeatherRepository;
import com.rionour.weather.web.share.model.HourWeatherData;
import com.rionour.weather.web.share.model.WeatherDataResponse;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MyCrawler extends WebCrawler {

    @Autowired
    private WeatherRepository repository;
    @Autowired
    private PlatformTransactionManager txManager;


    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return url.getDepth() == 0;
    }


    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getHtml();
            String jsonString = findJsonString(text);
            WeatherDataResponse jsonNode = parseJsonString(jsonString);
            Pattern pattern = Pattern.compile("/(\\d{9})\\.shtml");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                String code = matcher.group(1);
                resolveJsonData(jsonNode, code);
            } else {
                System.out.println("not found");
            }

        }
    }

    private String findJsonString(String lineData) {
        Pattern pattern = Pattern.compile("observe24h_data = (\\{.*\\});");
        Matcher matcher = pattern.matcher(lineData);
        if (matcher.find()) {
            String result = matcher.group(1);
            return result;
        } else {
            return null;
        }
    }

    private WeatherDataResponse parseJsonString(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, WeatherDataResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resolveJsonData(WeatherDataResponse jsonNode, String code) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            Weather model = new Weather();
            model.setCity(jsonNode.getInner().getCity());
            model.setCode(code);
            List<HourWeatherData> hourWeatherDataList = jsonNode.getInner().getHoureDataList();
            HourWeatherData weatherData = hourWeatherDataList.get(0);
            model.setHour(weatherData.getHour());
            model.setData1(weatherData.getHour());
            model.setData2(weatherData.getOd22());
            model.setData3(weatherData.getOd23());
            model.setData4(weatherData.getOd24());
            model.setData5(weatherData.getOd25());
            model.setData6(weatherData.getOd26());
            model.setData7(weatherData.getOd27());
            model.setData8(weatherData.getOd28());
            model.setDay(new Date());
            repository.save(model);
            txManager.commit(status);
        } catch (Throwable e) {
            e.printStackTrace();
            txManager.rollback(status);
        }
    }


}