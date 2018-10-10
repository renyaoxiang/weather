package com.rionour.weather.web.service.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rionour.weather.web.model.WeatherCrawl;
import com.rionour.weather.web.model.WeatherCrawlRepository;
import com.rionour.weather.web.share.model.HourWeatherData;
import com.rionour.weather.web.share.model.WeatherDataResponse;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private WeatherCrawlRepository repository;

    private PlatformTransactionManager txManager;

    public MyCrawler(WeatherCrawlRepository repository, PlatformTransactionManager txManager) {
        this.repository = repository;
        this.txManager = txManager;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return url.getDepth() == 0;
    }

    public static void main(String[] args) {
        String url = "http://www.weather.com.cn/weather/101130601.shtml";
        Pattern pattern = Pattern.compile("weather/(.*)\\.shtml");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String code = matcher.group(1);
            System.out.println(code);
        } else {
            System.out.println("not found");
        }
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String code = this.parseCode(url);
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getHtml();
            String jsonString = findJsonString(text);
            WeatherDataResponse jsonNode = parseJsonString(jsonString);
            if (jsonNode != null && code != null) {
                resolveJsonData(jsonNode, code);
            }
        }
    }

    public String parseCode(String url) {
        Pattern pattern = Pattern.compile("weather/(.*)\\.shtml");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String code = matcher.group(1);
            return code;
        } else {
            return null;
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
        } catch (Throwable e) {
        }
        return null;
    }

    public void resolveJsonData(WeatherDataResponse jsonNode, String code) {

        List<HourWeatherData> hourWeatherDataList = jsonNode.getInner().getHoureDataList();
        boolean isToday = true;
        for (HourWeatherData weatherData : hourWeatherDataList) {

            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                WeatherCrawl model = new WeatherCrawl();
                model.setCity(jsonNode.getInner().getCity());
                model.setCode(code);
                model.setHour(weatherData.getOd21());
                model.setData1(weatherData.getOd21());
                model.setData2(weatherData.getOd22());
                model.setData3(weatherData.getOd23());
                model.setData4(weatherData.getOd24());
                model.setData5(weatherData.getOd25());
                model.setData6(weatherData.getOd26());
                model.setData7(weatherData.getOd27());
                model.setData8(weatherData.getOd28());
                model.setDay(this.getDate(isToday));
                repository.save(model);
                txManager.commit(status);
            } catch (Throwable e) {
                e.printStackTrace();
                txManager.rollback(status);
            }
            if (StringUtils.equals(weatherData.getOd21(), "00")) {
                isToday = false;
            }
        }

    }

    public Date getDate(boolean isToday) {
        return isToday ? new Date() : DateUtils.addDays(new Date(), -1);
    }

}