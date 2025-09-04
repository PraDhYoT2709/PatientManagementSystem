package com.pms.doctor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.doctor.entity.Doctor;
import com.pms.doctor.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDoctor_ShouldReturnCreatedDoctor() throws Exception {
        // Given
        Doctor doctor = Doctor.builder()
                .name("Dr. John Smith")
                .specialty("Cardiology")
                .email("dr.john@example.com")
                .phone("9876543210")
                .qualification("MD")
                .experience(10)
                .department("Cardiology")
                .consultationFee("500")
                .available(true)
                .build();

        when(doctorService.createDoctor(any(Doctor.class))).thenReturn(doctor);

        // When & Then
        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr. John Smith"))
                .andExpect(jsonPath("$.specialty").value("Cardiology"));
    }

    @Test
    void getAllDoctors_ShouldReturnPageOfDoctors() throws Exception {
        // Given
        Doctor doctor1 = Doctor.builder().id(1L).name("Dr. John Smith").build();
        Doctor doctor2 = Doctor.builder().id(2L).name("Dr. Jane Doe").build();
        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);
        Page<Doctor> doctorPage = new PageImpl<>(doctors, PageRequest.of(0, 10), 2);

        when(doctorService.getAllDoctors(any(Pageable.class))).thenReturn(doctorPage);

        // When & Then
        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getDoctorById_WhenDoctorExists_ShouldReturnDoctor() throws Exception {
        // Given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. John Smith")
                .email("dr.john@example.com")
                .build();

        when(doctorService.getDoctorById(1L)).thenReturn(Optional.of(doctor));

        // When & Then
        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. John Smith"));
    }

    @Test
    void getDoctorById_WhenDoctorNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(doctorService.getDoctorById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDoctorsBySpecialty_ShouldReturnDoctors() throws Exception {
        // Given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. John Smith")
                .specialty("Cardiology")
                .build();

        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorService.getDoctorsBySpecialty("Cardiology")).thenReturn(doctors);

        // When & Then
        mockMvc.perform(get("/api/doctors/specialty/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].specialty").value("Cardiology"));
    }

    @Test
    void getAvailableDoctors_ShouldReturnAvailableDoctors() throws Exception {
        // Given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. John Smith")
                .available(true)
                .build();

        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorService.getAvailableDoctors()).thenReturn(doctors);

        // When & Then
        mockMvc.perform(get("/api/doctors/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void searchDoctorsByName_ShouldReturnFilteredDoctors() throws Exception {
        // Given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. John Smith")
                .build();

        Page<Doctor> doctorPage = new PageImpl<>(Arrays.asList(doctor), PageRequest.of(0, 10), 1);
        when(doctorService.searchDoctorsByName(eq("John"), any(Pageable.class))).thenReturn(doctorPage);

        // When & Then
        mockMvc.perform(get("/api/doctors/search/name?name=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Dr. John Smith"));
    }

    @Test
    void updateDoctor_WhenDoctorExists_ShouldReturnUpdatedDoctor() throws Exception {
        // Given
        Doctor existingDoctor = Doctor.builder()
                .id(1L)
                .name("Dr. John Smith")
                .email("dr.john@example.com")
                .phone("9876543210")
                .build();

        Doctor updatedDoctor = Doctor.builder()
                .id(1L)
                .name("Dr. John Smith Updated")
                .email("dr.john@example.com")
                .phone("9876543210")
                .build();

        when(doctorService.updateDoctor(eq(1L), any(Doctor.class))).thenReturn(updatedDoctor);

        // When & Then
        mockMvc.perform(put("/api/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDoctor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. John Smith Updated"));
    }

    @Test
    void deleteDoctor_WhenDoctorExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(doctorService).deleteDoctor(1L);

        // When & Then
        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());
    }
}