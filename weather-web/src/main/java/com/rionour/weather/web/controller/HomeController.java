package com.rionour.weather.web.controller;

import org.apache.http.HttpStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/")
public class HomeController {
    @RequestMapping("/")
    public String index() {

//        return ServerResponse
//                .status(HttpStatus.SC_MOVED_TEMPORARILY)
//                .header("Location", "index")
//                .contentType(MediaType.TEXT_HTML)
//                .build();
        return "index.html";
    }

}
