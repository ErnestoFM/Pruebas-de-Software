package com.ticketmaster.demo.controller;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("message", "Algo salió mal, intenta de nuevo.");
        return "error"; // renderiza error.html
    }

    @RequestMapping("/*")
    public String handlePageNotFound(Model model) {
        model.addAttribute("message", "Esta página no existe xd.");
        return "error"; // renderiza error.html
    }
}
