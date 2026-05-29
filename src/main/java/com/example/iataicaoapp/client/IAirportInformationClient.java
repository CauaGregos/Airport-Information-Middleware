package com.example.iataicaoapp.client;

import com.example.iataicaoapp.domain.AirportInformation;

import java.util.Optional;

public interface IAirportInformationClient {
    Optional<AirportInformation> findByICAOCode(String icaoCode);
}
