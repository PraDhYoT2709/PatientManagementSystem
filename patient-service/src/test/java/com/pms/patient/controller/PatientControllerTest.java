package com.pms.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.patient.entity.Patient;
import com.pms.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPatient_ShouldReturnCreatedPatient() throws Exception {
        // Given
        Patient patient = Patient.builder()
                .name("John Doe")
                .age(30)
                .gender("Male")
                .disease("Fever")
                .email("john@example.com")
                .phone("9876543210")
                .admitted(true)
                .build();

        when(patientService.createPatient(any(Patient.class))).thenReturn(patient);

        // When & Then
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getAllPatients_ShouldReturnPageOfPatients() throws Exception {
        // Given
        Patient patient1 = Patient.builder().id(1L).name("John Doe").build();
        Patient patient2 = Patient.builder().id(2L).name("Jane Doe").build();
        List<Patient> patients = Arrays.asList(patient1, patient2);
        Page<Patient> patientPage = new PageImpl<>(patients, PageRequest.of(0, 10), 2);

        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(patientPage);

        // When & Then
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getPatientById_WhenPatientExists_ShouldReturnPatient() throws Exception {
        // Given
        Patient patient = Patient.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(patientService.getPatientById(1L)).thenReturn(Optional.of(patient));

        // When & Then
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getPatientById_WhenPatientNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePatient_WhenPatientExists_ShouldReturnUpdatedPatient() throws Exception {
        // Given
        Patient existingPatient = Patient.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phone("9876543210")
                .build();

        Patient updatedPatient = Patient.builder()
                .id(1L)
                .name("John Smith")
                .email("john@example.com")
                .phone("9876543210")
                .build();

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(updatedPatient);

        // When & Then
        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Smith"));
    }

    @Test
    void deletePatient_WhenPatientExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(patientService).deletePatient(1L);

        // When & Then
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchPatientsByDisease_ShouldReturnFilteredPatients() throws Exception {
        // Given
        Patient patient = Patient.builder()
                .id(1L)
                .name("John Doe")
                .disease("Fever")
                .build();

        Page<Patient> patientPage = new PageImpl<>(Arrays.asList(patient), PageRequest.of(0, 10), 1);
        when(patientService.searchPatientsByDisease(eq("Fever"), any(Pageable.class))).thenReturn(patientPage);

        // When & Then
        mockMvc.perform(get("/api/patients/search?disease=Fever"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].disease").value("Fever"));
    }
}