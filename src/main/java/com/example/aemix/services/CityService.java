package com.example.aemix.services;

import com.example.aemix.dto.responses.CityResponse;
import com.example.aemix.mappers.CityMapper;
import com.example.aemix.repositories.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {
    private final CityMapper cityMapper;

    private final CityRepository cityRepository;

    public List<CityResponse> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }
}

