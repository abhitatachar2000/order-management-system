package com.oms.catalog.controller;

import com.oms.catalog.service.CatelogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/catalog")
public class CatelogController {

    @Autowired
    private CatelogService catelogService;

    @GetMapping("/hello")
    public String hello() {
        return catelogService.helloWrold();
    }
}
