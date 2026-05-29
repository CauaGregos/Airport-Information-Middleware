package com.example.iataicaoapp.controller;

import com.example.iataicaoapp.domain.AirportInformation;
import com.example.iataicaoapp.dto.AirportInformationResponseDto;
import com.example.iataicaoapp.exception.ControllerValidateException;
import com.example.iataicaoapp.service.AirportInformationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/airport/codes")
public class AirportInformationController {

    private final AirportInformationService airportInformationService;

    public AirportInformationController(AirportInformationService airportInformationService) {
        this.airportInformationService = airportInformationService;
    }

    @GetMapping("/{icaoCode}")
    public AirportInformationResponseDto findByICAOCode(@PathVariable String icaoCode) {
        icaoCode = icaoCode.toUpperCase().trim();
        ValidateICAO(icaoCode);
        AirportInformation aviationCode = airportInformationService.findByICAOCode(icaoCode);
        return AirportInformationResponseDto.fromDomain(aviationCode);
    }

    private void ValidateICAO(String code){
        if(code.isBlank())
            throw new ControllerValidateException("ICAO code is required");
        if(code.length() != 4)
            throw new ControllerValidateException("ICAO always contains 4 chars.");
        String regexIcao = "^[A-Z]{4}$";
        if (!code.matches(regexIcao))
            throw new ControllerValidateException("ICAO airport code must have exactly 4 letters");
    }
}
