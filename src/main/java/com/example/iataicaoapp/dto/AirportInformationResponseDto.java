package com.example.iataicaoapp.dto;

import com.example.iataicaoapp.domain.AirportInformation;

public record AirportInformationResponseDto(
        String iataCode,
        String icaoCode,
        String name,
        String city,
        String country
) {
    public static AirportInformationResponseDto fromDomain(AirportInformation aviationCode) {
        return new AirportInformationResponseDto(
                aviationCode.iataCode(),
                aviationCode.icaoCode(),
                aviationCode.name(),
                aviationCode.city(),
                aviationCode.country()
        );
    }
}
