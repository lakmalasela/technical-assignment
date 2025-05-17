package org.assignment.service.impl;

import org.assignment.entity.CityEntity;
import org.assignment.repository.CityRepository;
import org.assignment.service.CityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public List<CityEntity> getAllCities() {
        return cityRepository.findAll();
    }
}
