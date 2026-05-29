package com.example.iataicaoapp.service;

import com.example.iataicaoapp.client.IAirportInformationClient;
import com.example.iataicaoapp.domain.AirportInformation;
import org.springframework.stereotype.Service;

@Service
public class AirportInformationService {

    private final IAirportInformationClient airportInformationClient;

    public AirportInformationService(IAirportInformationClient airportInformationClient) {
        this.airportInformationClient = airportInformationClient;
    }

    public AirportInformation findByICAOCode(String icaoCode) {
        return airportInformationClient.findByICAOCode(icaoCode)
                .orElseThrow(() -> new RuntimeException("Aviation code not found: " + icaoCode));
    }
}
