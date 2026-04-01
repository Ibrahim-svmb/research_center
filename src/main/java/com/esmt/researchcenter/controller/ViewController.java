package com.esmt.researchcenter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/participant")
    public String participantDashboard() {
        return "participant";
    }

    @GetMapping("/manager")
    public String managerDashboard() {
        return "manager";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }
}
