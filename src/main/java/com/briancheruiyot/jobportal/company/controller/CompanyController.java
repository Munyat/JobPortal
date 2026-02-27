package com.briancheruiyot.jobportal.company.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @GetMapping(version = "1.0")
    public ResponseEntity<String> getAllComapnies() {
        return ResponseEntity.ok("Company List");
    }

}
