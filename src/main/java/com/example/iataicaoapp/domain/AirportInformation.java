package com.example.iataicaoapp.domain;

public record AirportInformation(
        String iataCode,
        String icaoCode,
        String name,
        String city,
        String country
) {
}
