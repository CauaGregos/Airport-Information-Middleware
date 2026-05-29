package com.example.iataicaoapp.dto.aviationApi;

import com.example.iataicaoapp.domain.AirportInformation;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AviationApiResponseDto(
        @JsonProperty("airport_data")
        AirportDataDto airportData
) {
    public AirportInformation toAviationCode() {
        if (airportData == null) {
            return null;
        }

        return airportData.toAviationCode();
    }

    public record AirportDataDto(
            @JsonProperty("faa_ident")
            String iataCode,

            @JsonProperty("icao_ident")
            String icaoCode,

            @JsonProperty("airport_name")
            String name,

            @JsonProperty("city")
            String city,

            @JsonProperty("country")
            String country
    ) {
        public AirportInformation toAviationCode() {
            return new AirportInformation(iataCode, icaoCode, name, city, country);
        }
    }
}
