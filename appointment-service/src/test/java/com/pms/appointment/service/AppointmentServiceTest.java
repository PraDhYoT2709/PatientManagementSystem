package com.pms.appointment.service;

import com.pms.appointment.dto.DoctorDto;
import com.pms.appointment.dto.PatientDto;
import com.pms.appointment.entity.Appointment;
import com.pms.appointment.feign.DoctorServiceClient;
import com.pms.appointment.feign.PatientServiceClient;
import com.pms.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private DoctorServiceClient doctorServiceClient;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private String authorization = "Bearer token";

    @BeforeEach
    void setUp() {
        testAppointment = Appointment.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusDays(1))
                .reason("Regular checkup")
                .status(Appointment.Status.SCHEDULED)
                .patientId(1L)
                .doctorId(1L)
                .build();
    }

    @Test
    void createAppointment_ShouldReturnSavedAppointment() {
        // Given
        when(patientServiceClient.checkPatientExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(doctorServiceClient.checkDoctorExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(appointmentRepository.findByDoctorIdAndDateTime(1L, testAppointment.getDateTime()))
                .thenReturn(Arrays.asList());
        when(appointmentRepository.save(testAppointment)).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.createAppointment(testAppointment, authorization);

        // Then
        assertNotNull(result);
        assertEquals(testAppointment.getReason(), result.getReason());
        assertEquals(testAppointment.getStatus(), result.getStatus());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void createAppointment_WhenPatientNotExists_ShouldThrowException() {
        // Given
        when(patientServiceClient.checkPatientExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(false));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.createAppointment(testAppointment, authorization));
        assertEquals("Patient not found with id: 1", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_WhenDoctorNotExists_ShouldThrowException() {
        // Given
        when(patientServiceClient.checkPatientExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(doctorServiceClient.checkDoctorExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(false));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.createAppointment(testAppointment, authorization));
        assertEquals("Doctor not found with id: 1", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_WhenTimeConflict_ShouldThrowException() {
        // Given
        when(patientServiceClient.checkPatientExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(doctorServiceClient.checkDoctorExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(appointmentRepository.findByDoctorIdAndDateTime(1L, testAppointment.getDateTime()))
                .thenReturn(Arrays.asList(testAppointment));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.createAppointment(testAppointment, authorization));
        assertEquals("Doctor already has an appointment at this time", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void getAllAppointments_ShouldReturnPageOfAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        Page<Appointment> appointmentPage = new PageImpl<>(appointments, PageRequest.of(0, 10), 1);
        when(appointmentRepository.findAll(any(Pageable.class))).thenReturn(appointmentPage);

        // When
        Page<Appointment> result = appointmentService.getAllAppointments(PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testAppointment.getReason(), result.getContent().get(0).getReason());
    }

    @Test
    void getAppointmentById_WhenAppointmentExists_ShouldReturnAppointment() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        // When
        Optional<Appointment> result = appointmentService.getAppointmentById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testAppointment.getReason(), result.get().getReason());
    }

    @Test
    void getAppointmentById_WhenAppointmentNotExists_ShouldReturnEmpty() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Appointment> result = appointmentService.getAppointmentById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getAppointmentsByPatientId_ShouldReturnAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByPatientId(1L)).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentsByPatientId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getPatientId(), result.get(0).getPatientId());
    }

    @Test
    void getAppointmentsByDoctorId_ShouldReturnAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentsByDoctorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getDoctorId(), result.get(0).getDoctorId());
    }

    @Test
    void getAppointmentsByStatus_ShouldReturnAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentsByStatus(Appointment.Status.SCHEDULED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Appointment.Status.SCHEDULED, result.get(0).getStatus());
    }

    @Test
    void updateAppointment_WhenAppointmentExists_ShouldReturnUpdatedAppointment() {
        // Given
        Appointment updatedAppointment = Appointment.builder()
                .dateTime(LocalDateTime.now().plusDays(2))
                .reason("Follow-up")
                .status(Appointment.Status.SCHEDULED)
                .patientId(1L)
                .doctorId(1L)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(patientServiceClient.checkPatientExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(doctorServiceClient.checkDoctorExists(1L, authorization))
                .thenReturn(ResponseEntity.ok(true));
        when(appointmentRepository.findByDoctorIdAndDateTime(1L, updatedAppointment.getDateTime()))
                .thenReturn(Arrays.asList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);

        // When
        Appointment result = appointmentService.updateAppointment(1L, updatedAppointment, authorization);

        // Then
        assertNotNull(result);
        assertEquals("Follow-up", result.getReason());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_WhenAppointmentNotExists_ShouldThrowException() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.updateAppointment(1L, testAppointment, authorization));
        assertEquals("Appointment not found with id: 1", exception.getMessage());
    }

    @Test
    void deleteAppointment_WhenAppointmentExists_ShouldDeleteAppointment() {
        // Given
        when(appointmentRepository.existsById(1L)).thenReturn(true);

        // When
        appointmentService.deleteAppointment(1L);

        // Then
        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    void deleteAppointment_WhenAppointmentNotExists_ShouldThrowException() {
        // Given
        when(appointmentRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.deleteAppointment(1L));
        assertEquals("Appointment not found with id: 1", exception.getMessage());
        verify(appointmentRepository, never()).deleteById(any());
    }

    @Test
    void getPatientDetails_ShouldReturnPatient() {
        // Given
        PatientDto patient = new PatientDto();
        patient.setId(1L);
        patient.setName("John Doe");

        when(patientServiceClient.getPatientById(1L, authorization))
                .thenReturn(ResponseEntity.ok(patient));

        // When
        PatientDto result = appointmentService.getPatientDetails(1L, authorization);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getDoctorDetails_ShouldReturnDoctor() {
        // Given
        DoctorDto doctor = new DoctorDto();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");

        when(doctorServiceClient.getDoctorById(1L, authorization))
                .thenReturn(ResponseEntity.ok(doctor));

        // When
        DoctorDto result = appointmentService.getDoctorDetails(1L, authorization);

        // Then
        assertNotNull(result);
        assertEquals("Dr. Smith", result.getName());
    }
}