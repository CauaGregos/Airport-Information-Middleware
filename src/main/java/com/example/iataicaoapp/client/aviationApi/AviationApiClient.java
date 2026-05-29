package com.example.iataicaoapp.client.aviationApi;

import com.example.iataicaoapp.client.IAirportInformationClient;
import com.example.iataicaoapp.domain.AirportInformation;
import com.example.iataicaoapp.dto.aviationApi.AviationApiResponseDto;
import com.example.iataicaoapp.exception.AviationApiClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class AviationApiClient implements IAirportInformationClient {

    private final RestClient restClient;
    private final String airportPath;

    public AviationApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${aviation.api.base-url}") String baseUrl,
            @Value("${aviation.api.airport-path}") String airportPath
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.airportPath = airportPath;
    }

    public Optional<AirportInformation> findByICAOCode(String code) {
        try {
            AviationApiResponseDto response = restClient.get()
                    .uri(airportPath, code)
                    .retrieve()
                    .body(AviationApiResponseDto.class);

            return Optional.ofNullable(response)
                    .map(AviationApiResponseDto::toAviationCode);
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new AviationApiClientException("The code ("+ code +") not exist");
        }
        catch (HttpClientErrorException.NotFound exception) {
            throw new AviationApiClientException("External api is down with exception message: " + exception.getMessage());
        }
    }
}
