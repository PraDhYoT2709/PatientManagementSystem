package com.pms.appointment.service;

import com.pms.appointment.dto.DoctorDto;
import com.pms.appointment.dto.PatientDto;
import com.pms.appointment.entity.Appointment;
import com.pms.appointment.feign.DoctorServiceClient;
import com.pms.appointment.feign.PatientServiceClient;
import com.pms.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    public Appointment createAppointment(Appointment appointment, String authorization) {
        // Validate patient exists
        ResponseEntity<Boolean> patientExists = patientServiceClient.checkPatientExists(
                appointment.getPatientId(), authorization);
        if (patientExists.getBody() == null || !patientExists.getBody()) {
            throw new RuntimeException("Patient not found with id: " + appointment.getPatientId());
        }

        // Validate doctor exists
        ResponseEntity<Boolean> doctorExists = doctorServiceClient.checkDoctorExists(
                appointment.getDoctorId(), authorization);
        if (doctorExists.getBody() == null || !doctorExists.getBody()) {
            throw new RuntimeException("Doctor not found with id: " + appointment.getDoctorId());
        }

        // Check for scheduling conflicts
        List<Appointment> conflictingAppointments = appointmentRepository
                .findByDoctorIdAndDateTime(appointment.getDoctorId(), appointment.getDateTime());
        if (!conflictingAppointments.isEmpty()) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }

        // Set default status if not provided
        if (appointment.getStatus() == null) {
            appointment.setStatus(Appointment.Status.SCHEDULED);
        }

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByDateTimeBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatientAndDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByPatientIdAndDateTimeBetween(patientId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctorAndDateRange(Long doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByDoctorIdAndDateTimeBetween(doctorId, startDate, endDate);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails, String authorization) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // If patient ID is being changed, validate new patient exists
        if (!appointment.getPatientId().equals(appointmentDetails.getPatientId())) {
            ResponseEntity<Boolean> patientExists = patientServiceClient.checkPatientExists(
                    appointmentDetails.getPatientId(), authorization);
            if (patientExists.getBody() == null || !patientExists.getBody()) {
                throw new RuntimeException("Patient not found with id: " + appointmentDetails.getPatientId());
            }
        }

        // If doctor ID is being changed, validate new doctor exists
        if (!appointment.getDoctorId().equals(appointmentDetails.getDoctorId())) {
            ResponseEntity<Boolean> doctorExists = doctorServiceClient.checkDoctorExists(
                    appointmentDetails.getDoctorId(), authorization);
            if (doctorExists.getBody() == null || !doctorExists.getBody()) {
                throw new RuntimeException("Doctor not found with id: " + appointmentDetails.getDoctorId());
            }
        }

        // If date/time is being changed, check for conflicts
        if (!appointment.getDateTime().equals(appointmentDetails.getDateTime()) ||
            !appointment.getDoctorId().equals(appointmentDetails.getDoctorId())) {
            List<Appointment> conflictingAppointments = appointmentRepository
                    .findByDoctorIdAndDateTime(appointmentDetails.getDoctorId(), appointmentDetails.getDateTime());
            // Exclude current appointment from conflict check
            conflictingAppointments.removeIf(apt -> apt.getId().equals(id));
            if (!conflictingAppointments.isEmpty()) {
                throw new RuntimeException("Doctor already has an appointment at this time");
            }
        }

        appointment.setDateTime(appointmentDetails.getDateTime());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setStatus(appointmentDetails.getStatus());
        appointment.setPatientId(appointmentDetails.getPatientId());
        appointment.setDoctorId(appointmentDetails.getDoctorId());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return appointmentRepository.existsById(id);
    }

    public PatientDto getPatientDetails(Long patientId, String authorization) {
        ResponseEntity<PatientDto> response = patientServiceClient.getPatientById(patientId, authorization);
        return response.getBody();
    }

    public DoctorDto getDoctorDetails(Long doctorId, String authorization) {
        ResponseEntity<DoctorDto> response = doctorServiceClient.getDoctorById(doctorId, authorization);
        return response.getBody();
    }
}