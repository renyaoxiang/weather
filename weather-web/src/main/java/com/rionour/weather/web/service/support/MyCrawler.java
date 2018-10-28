package com.rionour.weather.web.service.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rionour.weather.web.model.DayinfoCrawlRepository;
import com.rionour.weather.web.model.DayinfoCrawl;
import com.rionour.weather.web.model.WeatherCrawl;
import com.rionour.weather.web.model.WeatherCrawlRepository;
import com.rionour.weather.web.share.model.HourWeatherData;
import com.rionour.weather.web.share.model.WeatherDataResponse;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private WeatherCrawlRepository repository;

    private DayinfoCrawlRepository dayinfoCrawlRepository;

    private PlatformTransactionManager txManager;

    public MyCrawler(WeatherCrawlRepository repository, DayinfoCrawlRepository dayinfoCrawlRepository, PlatformTransactionManager txManager) {
        this.repository = repository;
        this.dayinfoCrawlRepository = dayinfoCrawlRepository;
        this.txManager = txManager;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return url.getDepth() == 0;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        String url = "http://www.weather.com.cn/weather1d/101130601.shtml";

        URI uri = new URI(url);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        InputStream content = connection.getInputStream();
        String newString = IOUtils.toString(content, Charset.forName("utf8"));
        System.out.println(findSunriseString(newString));
        System.out.println(findSunsetString(newString));
        IOUtils.close(connection);
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
            Context context = new Context();
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getHtml();
            saveWeatherData(context, code, text);
            saveDayinfoData(context, code, text);
        }
    }

    public class Context extends HashMap<String, String> {
        private static final String city = "city";
        private static final String code = "code";

        public String getCity() {
            return this.getOrDefault(city, null);
        }

        public void setCity(String city) {
            this.put(Context.city, city);
        }

        public String getCode() {
            return this.getOrDefault(code, null);
        }

        public void setCode(String code) {
            this.put(Context.code, code);
        }
    }

    private void saveDayinfoData(Context context, String code, String text) {
        String sunriseString = findSunriseString(text);
        String sunsetString = findSunsetString(text);
        if (sunriseString != null && sunsetString != null && code != null) {
            resolveDayinfoJsonData(context, sunriseString, sunsetString);
        }
    }

    private static String findSunriseString(String text) {
        Pattern pattern = Pattern.compile("\\>日出 (.*)\\<");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String result = matcher.group(1);
            return result;
        } else {
            return null;
        }
    }

    private static String findSunsetString(String text) {
        Pattern pattern = Pattern.compile("日落 (\\d\\d:\\d\\d)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String result = matcher.group(1);
            return result;
        } else {
            return null;
        }
    }

    private void saveWeatherData(Context context, String code, String text) {
        String jsonString = findJsonString(text);
        WeatherDataResponse jsonNode = parseJsonString(jsonString);
        if (jsonNode != null && code != null) {
            resolveJsonData(context, jsonNode, code);
        }
    }

    private void resolveDayinfoJsonData(Context context, String sunriseString, String sunsetString) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = txManager.getTransaction(def);
        try {

            DayinfoCrawl model = new DayinfoCrawl();
            model.setCity(context.getCity());
            model.setCode(context.getCode());
            model.setSunrise(sunriseString);
            model.setSunset(sunsetString);
            model.setDay(new Date());
            dayinfoCrawlRepository.save(model);
        } catch (Throwable e) {
            e.printStackTrace();
            txManager.commit(status);
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

    public void resolveJsonData(Context context, WeatherDataResponse jsonNode, String code) {

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