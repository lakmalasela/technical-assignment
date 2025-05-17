package org.assignment.controller;


import org.assignment.dto.ResponseMessage;
import org.assignment.entity.CityEntity;
import org.assignment.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<CityEntity>>> getAllCountries() {
        List<CityEntity> countryList = cityService.getAllCities();
        ResponseMessage<List<CityEntity>> response = new ResponseMessage<>("Countries fetched successfully", countryList);
        return ResponseEntity.ok(response);
    }

}
