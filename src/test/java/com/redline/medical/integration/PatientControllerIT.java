package com.redline.medical.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPatients_default() throws Exception {
        mockMvc.perform(get("/api/v1/patients").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(9)))
                .andExpect(jsonPath("$.data", hasSize(9)))
                .andExpect(jsonPath("$.data[0].firstName", is("Andriy")))
                .andExpect(jsonPath("$.data[0].lastName", is("Kushnir")))
                .andExpect(jsonPath("$.data[0].lastVisits", notNullValue()));

    }

    @Test
    void getPatients_pagination() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "1")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(9)))
                .andExpect(jsonPath("$.data.length()", is(3)))
                .andExpect(jsonPath("$.data[0].firstName", is("Iryna")))
                .andExpect(jsonPath("$.data[0].lastName", is("Lysenko")));
    }

    @Test
    void getPatients_search() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("search", "Kravchuk")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].lastName", everyItem(is("Kravchuk"))));
    }

    @Test
    void getPatients_filterByDoctorIds() throws Exception {
        // doctor id 3 -> Sofia Melnyk
        mockMvc.perform(get("/api/v1/patients")
                        .param("doctorIds", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].lastVisits[*].doctor.firstName", everyItem(is("Sofia"))));
    }
}
