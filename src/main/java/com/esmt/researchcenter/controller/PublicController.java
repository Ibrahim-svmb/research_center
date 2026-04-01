package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.ResearchDomain;
import com.esmt.researchcenter.service.ResearchDomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public", description = "Public APIs")
public class PublicController {

    @Autowired
    private ResearchDomainService researchDomainService;

    @Operation(summary = "Get all domains", description = "Retrieve a list of all research domains publicly")
    @GetMapping("/domains")
    public List<ResearchDomain> getAllDomains() {
        return researchDomainService.getAllDomains();
    }
}
