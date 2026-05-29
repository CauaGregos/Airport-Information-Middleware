package com.example.iataicaoapp.controller;

import com.example.iataicaoapp.IataIcaoAppApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = IataIcaoAppApplication.class)
@AutoConfigureMockRestServiceServer
class AirportInformationControllerTest {

    private static final String AVIATION_API_SBGR_URL =
            "https://api-v2.aviationapi.com/v2/charts?airport=SBGR";

    private static final String AIRPORT_INFORMATION_RESPONSE = """
            {
              "airport_data": {
                "faa_ident": "GRU",
                "icao_ident": "SBGR",
                "airport_name": "Guarulhos International Airport",
                "city": "Sao Paulo",
                "country": "Brazil"
              }
            }
            """;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockRestServiceServer.reset();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void findByICAOCodeWhenCodeIsValidReturnsAirportInformation() throws Exception {
        mockRestServiceServer.expect(requestTo(AVIATION_API_SBGR_URL))
                .andExpect(method(GET))
                .andRespond(withSuccess(AIRPORT_INFORMATION_RESPONSE, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/airport/codes/{icaoCode}", "SBGR"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.iataCode").value("GRU"))
                .andExpect(jsonPath("$.icaoCode").value("SBGR"))
                .andExpect(jsonPath("$.name").value("Guarulhos International Airport"))
                .andExpect(jsonPath("$.city").value("Sao Paulo"))
                .andExpect(jsonPath("$.country").value("Brazil"));

        mockRestServiceServer.verify();
    }

    @Test
    void findByICAOCodeWhenCodeHasSpacesAndLowercaseNormalizesBeforeCallingService() throws Exception {
        mockRestServiceServer.expect(requestTo(AVIATION_API_SBGR_URL))
                .andExpect(method(GET))
                .andRespond(withSuccess(AIRPORT_INFORMATION_RESPONSE, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/airport/codes/{icaoCode}", " sbgr "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.icaoCode").value("SBGR"));

        mockRestServiceServer.verify();
    }

    @Test
    void findByICAOCodeWhenCodeIsBlankReturnsForbiddenAndDoesNotCallService() throws Exception {
        mockMvc.perform(get("/api/airport/codes/{icaoCode}", "    "))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("ICAO code is required"))
                .andExpect(jsonPath("$.path").value("/api/airport/codes/%20%20%20%20"));

        mockRestServiceServer.verify();
    }

    @Test
    void findByICAOCodeWhenCodeLengthIsInvalidReturnsForbiddenAndDoesNotCallService() throws Exception {
        mockMvc.perform(get("/api/airport/codes/{icaoCode}", "SBG"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("ICAO always contains 4 chars."))
                .andExpect(jsonPath("$.path").value("/api/airport/codes/SBG"));

        mockRestServiceServer.verify();
    }

    @Test
    void findByICAOCodeWhenCodeContainsInvalidCharactersReturnsForbiddenAndDoesNotCallService() throws Exception {
        mockMvc.perform(get("/api/airport/codes/{icaoCode}", "SBG1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("ICAO airport code must have exactly 4 letters"))
                .andExpect(jsonPath("$.path").value("/api/airport/codes/SBG1"));

        mockRestServiceServer.verify();
    }

    @Test
    void findByICAOCodeWhenServiceThrowsAviationApiClientExceptionReturnsBadRequest() throws Exception {
        mockRestServiceServer.expect(requestTo(AVIATION_API_SBGR_URL))
                .andExpect(method(GET))
                .andRespond(withServerError());

        mockMvc.perform(get("/api/airport/codes/{icaoCode}", "SBGR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("The code (SBGR) not exist"))
                .andExpect(jsonPath("$.path").value("/api/airport/codes/SBGR"));

        mockRestServiceServer.verify();
    }
}
