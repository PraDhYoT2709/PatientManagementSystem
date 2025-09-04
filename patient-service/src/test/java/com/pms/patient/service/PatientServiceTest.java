package com.pms.patient.service;

import com.pms.patient.entity.Patient;
import com.pms.patient.repository.PatientRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .id(1L)
                .name("John Doe")
                .age(30)
                .gender("Male")
                .disease("Fever")
                .email("john@example.com")
                .phone("9876543210")
                .admitted(true)
                .admittedDate(LocalDate.now())
                .build();
    }

    @Test
    void createPatient_ShouldReturnSavedPatient() {
        // Given
        when(patientRepository.findByEmail(testPatient.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.findByPhone(testPatient.getPhone())).thenReturn(Optional.empty());
        when(patientRepository.save(testPatient)).thenReturn(testPatient);

        // When
        Patient result = patientService.createPatient(testPatient);

        // Then
        assertNotNull(result);
        assertEquals(testPatient.getName(), result.getName());
        assertEquals(testPatient.getEmail(), result.getEmail());
        verify(patientRepository).save(testPatient);
    }

    @Test
    void createPatient_WhenEmailExists_ShouldThrowException() {
        // Given
        when(patientRepository.findByEmail(testPatient.getEmail())).thenReturn(Optional.of(testPatient));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.createPatient(testPatient));
        assertEquals("Patient with email " + testPatient.getEmail() + " already exists", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void createPatient_WhenPhoneExists_ShouldThrowException() {
        // Given
        when(patientRepository.findByEmail(testPatient.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.findByPhone(testPatient.getPhone())).thenReturn(Optional.of(testPatient));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.createPatient(testPatient));
        assertEquals("Patient with phone " + testPatient.getPhone() + " already exists", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void getAllPatients_ShouldReturnPageOfPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        Page<Patient> patientPage = new PageImpl<>(patients, PageRequest.of(0, 10), 1);
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(patientPage);

        // When
        Page<Patient> result = patientService.getAllPatients(PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testPatient.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getPatientById_WhenPatientExists_ShouldReturnPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        // When
        Optional<Patient> result = patientService.getPatientById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPatient.getName(), result.get().getName());
    }

    @Test
    void getPatientById_WhenPatientNotExists_ShouldReturnEmpty() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Patient> result = patientService.getPatientById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getPatientsByDoctorId_ShouldReturnPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByDoctorId(1L)).thenReturn(patients);

        // When
        List<Patient> result = patientService.getPatientsByDoctorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPatient.getName(), result.get(0).getName());
    }

    @Test
    void updatePatient_WhenPatientExists_ShouldReturnUpdatedPatient() {
        // Given
        Patient updatedPatient = Patient.builder()
                .name("John Smith")
                .email("john@example.com")
                .phone("9876543210")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.findByEmail(updatedPatient.getEmail())).thenReturn(Optional.of(testPatient));
        when(patientRepository.findByPhone(updatedPatient.getPhone())).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        // When
        Patient result = patientService.updatePatient(1L, updatedPatient);

        // Then
        assertNotNull(result);
        assertEquals("John Smith", result.getName());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_WhenPatientNotExists_ShouldThrowException() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.updatePatient(1L, testPatient));
        assertEquals("Patient not found with id: 1", exception.getMessage());
    }

    @Test
    void deletePatient_WhenPatientExists_ShouldDeletePatient() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);

        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void deletePatient_WhenPatientNotExists_ShouldThrowException() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.deletePatient(1L));
        assertEquals("Patient not found with id: 1", exception.getMessage());
        verify(patientRepository, never()).deleteById(any());
    }

    @Test
    void searchPatientsByDisease_ShouldReturnFilteredPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        Page<Patient> patientPage = new PageImpl<>(patients, PageRequest.of(0, 10), 1);
        when(patientRepository.findByDiseaseContainingIgnoreCase(eq("Fever"), any(Pageable.class)))
                .thenReturn(patientPage);

        // When
        Page<Patient> result = patientService.searchPatientsByDisease("Fever", PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Fever", result.getContent().get(0).getDisease());
    }

    @Test
    void getAdmittedPatients_ShouldReturnAdmittedPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByAdmittedTrue()).thenReturn(patients);

        // When
        List<Patient> result = patientService.getAdmittedPatients();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAdmitted());
    }
}