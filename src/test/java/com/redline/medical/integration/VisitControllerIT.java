package com.redline.medical.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redline.medical.entities.Visit;
import com.redline.medical.repositories.VisitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VisitControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VisitRepository visitRepository;

    @Test
    void createVisit_success() throws Exception {
        // Doctor 1 timezone: Europe/Kyiv
        LocalDateTime start = LocalDateTime.parse("2025-10-01T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2025-10-01T10:30:00");

        String payload = "{" +
                "\"start\":\"" + start + "\"," +
                "\"end\":\"" + end + "\"," +
                "\"doctorId\":1," +
                "\"patientId\":1" +
                "}";

        String response = mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.start", is("2025-10-01T10:00:00")))
                .andExpect(jsonPath("$.end", is("2025-10-01T10:30:00")))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();
        Visit saved = visitRepository.findById(id).orElseThrow();

        Instant expectedStartUtc = start.atZone(ZoneId.of("Europe/Kyiv")).toInstant();
        Instant expectedEndUtc = end.atZone(ZoneId.of("Europe/Kyiv")).toInstant();

        assertThat(saved.getStartDateTime()).isEqualTo(expectedStartUtc);
        assertThat(saved.getEndDateTime()).isEqualTo(expectedEndUtc);
    }

    @Test
    void createVisit_validationError() throws Exception {
        String invalidPayload = "{" +
                "\"start\":null," +
                "\"end\":\"2025-10-01T10:30:00\"," +
                "\"patientId\":1" +
                "}";

        mockMvc.perform(post("/api/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }
}
