package com.toyproject.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/policy")
public class TermsController {

    @GetMapping("terms")
    public String termsPage(){
        return "policy/terms";
    }

}
