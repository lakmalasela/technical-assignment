package org.assignment.controller;

import org.assignment.dto.CustomerRequestDto;
import org.assignment.dto.CustomerResponseDto;
import org.assignment.dto.ResponseMessage;
import org.assignment.entity.CountryEntity;
import org.assignment.service.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }



    @GetMapping
    public ResponseEntity<ResponseMessage<List<CountryEntity>>> getAllCountries() {
        List<CountryEntity> countryList = countryService.getAllCountries();
        ResponseMessage<List<CountryEntity>> response = new ResponseMessage<>("Countries fetched successfully", countryList);
        return ResponseEntity.ok(response);
    }


}
