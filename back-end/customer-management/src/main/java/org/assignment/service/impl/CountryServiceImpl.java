package org.assignment.service.impl;

import org.assignment.entity.CountryEntity;
import org.assignment.repository.CountryRepository;
import org.assignment.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public List<CountryEntity> getAllCountries() {
        return countryRepository.findAll();
    }
}
