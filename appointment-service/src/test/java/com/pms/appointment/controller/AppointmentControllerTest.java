package com.pms.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.appointment.dto.DoctorDto;
import com.pms.appointment.dto.PatientDto;
import com.pms.appointment.entity.Appointment;
import com.pms.appointment.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAppointment_ShouldReturnCreatedAppointment() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .reason("Regular checkup")
                .status(Appointment.Status.SCHEDULED)
                .patientId(1L)
                .doctorId(1L)
                .build();

        when(appointmentService.createAppointment(any(Appointment.class), anyString())).thenReturn(appointment);

        // When & Then
        mockMvc.perform(post("/api/appointments")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value("Regular checkup"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void getAllAppointments_ShouldReturnPageOfAppointments() throws Exception {
        // Given
        Appointment appointment1 = Appointment.builder().id(1L).reason("Checkup").build();
        Appointment appointment2 = Appointment.builder().id(2L).reason("Follow-up").build();
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);
        Page<Appointment> appointmentPage = new PageImpl<>(appointments, PageRequest.of(0, 10), 2);

        when(appointmentService.getAllAppointments(any(Pageable.class))).thenReturn(appointmentPage);

        // When & Then
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAppointmentById_WhenAppointmentExists_ShouldReturnAppointment() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .id(1L)
                .reason("Regular checkup")
                .status(Appointment.Status.SCHEDULED)
                .build();

        when(appointmentService.getAppointmentById(1L)).thenReturn(Optional.of(appointment));

        // When & Then
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reason").value("Regular checkup"));
    }

    @Test
    void getAppointmentById_WhenAppointmentNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAppointmentsByPatientId_ShouldReturnAppointments() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .reason("Checkup")
                .build();

        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentService.getAppointmentsByPatientId(1L)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].patientId").value(1));
    }

    @Test
    void getAppointmentsByDoctorId_ShouldReturnAppointments() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .id(1L)
                .doctorId(1L)
                .reason("Checkup")
                .build();

        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentService.getAppointmentsByDoctorId(1L)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/doctor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorId").value(1));
    }

    @Test
    void getAppointmentsByStatus_ShouldReturnAppointments() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(Appointment.Status.SCHEDULED)
                .build();

        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentService.getAppointmentsByStatus(Appointment.Status.SCHEDULED)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/status/SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    void getPatientDetails_ShouldReturnPatient() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .build();

        PatientDto patient = new PatientDto();
        patient.setId(1L);
        patient.setName("John Doe");

        when(appointmentService.getAppointmentById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentService.getPatientDetails(1L, "Bearer token")).thenReturn(patient);

        // When & Then
        mockMvc.perform(get("/api/appointments/1/patient")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getDoctorDetails_ShouldReturnDoctor() throws Exception {
        // Given
        Appointment appointment = Appointment.builder()
                .id(1L)
                .doctorId(1L)
                .build();

        DoctorDto doctor = new DoctorDto();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");

        when(appointmentService.getAppointmentById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentService.getDoctorDetails(1L, "Bearer token")).thenReturn(doctor);

        // When & Then
        mockMvc.perform(get("/api/appointments/1/doctor")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Smith"));
    }

    @Test
    void updateAppointment_WhenAppointmentExists_ShouldReturnUpdatedAppointment() throws Exception {
        // Given
        Appointment existingAppointment = Appointment.builder()
                .id(1L)
                .reason("Checkup")
                .build();

        Appointment updatedAppointment = Appointment.builder()
                .id(1L)
                .reason("Follow-up")
                .build();

        when(appointmentService.updateAppointment(eq(1L), any(Appointment.class), anyString()))
                .thenReturn(updatedAppointment);

        // When & Then
        mockMvc.perform(put("/api/appointments/1")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAppointment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Follow-up"));
    }

    @Test
    void deleteAppointment_WhenAppointmentExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(appointmentService).deleteAppointment(1L);

        // When & Then
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }
}