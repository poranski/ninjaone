package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.service.DataLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping()
public class DatabaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseController.class);
    private final DataLoaderService dataLoaderService;

    public DatabaseController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @ResponseBody
    @GetMapping("/reseed")
    @Operation(summary = "Reseeds the database to default state")
    public ResponseEntity<String> reseedDatabase() {
        LOGGER.info("Reseeding the database");
        dataLoaderService.reseedDataBase();
        return ResponseEntity.ok("success");
    }
}