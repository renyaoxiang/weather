package com.rionour.weather.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @RequestMapping("path")
    public String path(String url) {
        return "forward:" + url;
    }

    @RequestMapping("")
    public String index() {
        return "redirect:index.html";
    }
}
