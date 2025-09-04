package com.pms.doctor.service;

import com.pms.doctor.entity.Doctor;
import com.pms.doctor.repository.DoctorRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testDoctor = Doctor.builder()
                .id(1L)
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
    }

    @Test
    void createDoctor_ShouldReturnSavedDoctor() {
        // Given
        when(doctorRepository.findByEmail(testDoctor.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.findByPhone(testDoctor.getPhone())).thenReturn(Optional.empty());
        when(doctorRepository.save(testDoctor)).thenReturn(testDoctor);

        // When
        Doctor result = doctorService.createDoctor(testDoctor);

        // Then
        assertNotNull(result);
        assertEquals(testDoctor.getName(), result.getName());
        assertEquals(testDoctor.getEmail(), result.getEmail());
        verify(doctorRepository).save(testDoctor);
    }

    @Test
    void createDoctor_WhenEmailExists_ShouldThrowException() {
        // Given
        when(doctorRepository.findByEmail(testDoctor.getEmail())).thenReturn(Optional.of(testDoctor));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> doctorService.createDoctor(testDoctor));
        assertEquals("Doctor with email " + testDoctor.getEmail() + " already exists", exception.getMessage());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctor_WhenPhoneExists_ShouldThrowException() {
        // Given
        when(doctorRepository.findByEmail(testDoctor.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.findByPhone(testDoctor.getPhone())).thenReturn(Optional.of(testDoctor));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> doctorService.createDoctor(testDoctor));
        assertEquals("Doctor with phone " + testDoctor.getPhone() + " already exists", exception.getMessage());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void getAllDoctors_ShouldReturnPageOfDoctors() {
        // Given
        List<Doctor> doctors = Arrays.asList(testDoctor);
        Page<Doctor> doctorPage = new PageImpl<>(doctors, PageRequest.of(0, 10), 1);
        when(doctorRepository.findAll(any(Pageable.class))).thenReturn(doctorPage);

        // When
        Page<Doctor> result = doctorService.getAllDoctors(PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testDoctor.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getDoctorById_WhenDoctorExists_ShouldReturnDoctor() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When
        Optional<Doctor> result = doctorService.getDoctorById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDoctor.getName(), result.get().getName());
    }

    @Test
    void getDoctorById_WhenDoctorNotExists_ShouldReturnEmpty() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Doctor> result = doctorService.getDoctorById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getDoctorsBySpecialty_ShouldReturnDoctors() {
        // Given
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findBySpecialty("Cardiology")).thenReturn(doctors);

        // When
        List<Doctor> result = doctorService.getDoctorsBySpecialty("Cardiology");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDoctor.getSpecialty(), result.get(0).getSpecialty());
    }

    @Test
    void getAvailableDoctors_ShouldReturnAvailableDoctors() {
        // Given
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findByAvailableTrue()).thenReturn(doctors);

        // When
        List<Doctor> result = doctorService.getAvailableDoctors();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    void updateDoctor_WhenDoctorExists_ShouldReturnUpdatedDoctor() {
        // Given
        Doctor updatedDoctor = Doctor.builder()
                .name("Dr. John Smith Updated")
                .email("dr.john@example.com")
                .phone("9876543210")
                .build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.findByEmail(updatedDoctor.getEmail())).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.findByPhone(updatedDoctor.getPhone())).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        // When
        Doctor result = doctorService.updateDoctor(1L, updatedDoctor);

        // Then
        assertNotNull(result);
        assertEquals("Dr. John Smith Updated", result.getName());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_WhenDoctorNotExists_ShouldThrowException() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> doctorService.updateDoctor(1L, testDoctor));
        assertEquals("Doctor not found with id: 1", exception.getMessage());
    }

    @Test
    void deleteDoctor_WhenDoctorExists_ShouldDeleteDoctor() {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(true);

        // When
        doctorService.deleteDoctor(1L);

        // Then
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void deleteDoctor_WhenDoctorNotExists_ShouldThrowException() {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> doctorService.deleteDoctor(1L));
        assertEquals("Doctor not found with id: 1", exception.getMessage());
        verify(doctorRepository, never()).deleteById(any());
    }

    @Test
    void searchDoctorsByName_ShouldReturnFilteredDoctors() {
        // Given
        List<Doctor> doctors = Arrays.asList(testDoctor);
        Page<Doctor> doctorPage = new PageImpl<>(doctors, PageRequest.of(0, 10), 1);
        when(doctorRepository.findByNameContainingIgnoreCase(eq("John"), any(Pageable.class)))
                .thenReturn(doctorPage);

        // When
        Page<Doctor> result = doctorService.searchDoctorsByName("John", PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Dr. John Smith", result.getContent().get(0).getName());
    }

    @Test
    void searchDoctorsBySpecialty_ShouldReturnFilteredDoctors() {
        // Given
        List<Doctor> doctors = Arrays.asList(testDoctor);
        Page<Doctor> doctorPage = new PageImpl<>(doctors, PageRequest.of(0, 10), 1);
        when(doctorRepository.findBySpecialtyContainingIgnoreCase(eq("Cardiology"), any(Pageable.class)))
                .thenReturn(doctorPage);

        // When
        Page<Doctor> result = doctorService.searchDoctorsBySpecialty("Cardiology", PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Cardiology", result.getContent().get(0).getSpecialty());
    }
}