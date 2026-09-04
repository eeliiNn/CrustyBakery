package com.crustybakery.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPagesController {

    @GetMapping("/error/403")
    public String accesoDenegado() {
        return "error/403";
    }
}
