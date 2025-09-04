package com.pms.patient.service;

import com.pms.patient.entity.Patient;
import com.pms.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient createPatient(Patient patient) {
        // Check if email already exists
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new RuntimeException("Patient with email " + patient.getEmail() + " already exists");
        }
        
        // Check if phone already exists
        if (patientRepository.findByPhone(patient.getPhone()).isPresent()) {
            throw new RuntimeException("Patient with phone " + patient.getPhone() + " already exists");
        }
        
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Patient> getPatientsByDoctorId(Long doctorId) {
        return patientRepository.findByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    public Page<Patient> searchPatientsByDisease(String disease, Pageable pageable) {
        return patientRepository.findByDiseaseContainingIgnoreCase(disease, pageable);
    }

    @Transactional(readOnly = true)
    public List<Patient> getAdmittedPatients() {
        return patientRepository.findByAdmittedTrue();
    }

    @Transactional(readOnly = true)
    public List<Patient> getDischargedPatients() {
        return patientRepository.findByAdmittedFalse();
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // Check email uniqueness if changed
        if (!patient.getEmail().equals(patientDetails.getEmail()) && 
            patientRepository.findByEmail(patientDetails.getEmail()).isPresent()) {
            throw new RuntimeException("Patient with email " + patientDetails.getEmail() + " already exists");
        }

        // Check phone uniqueness if changed
        if (!patient.getPhone().equals(patientDetails.getPhone()) && 
            patientRepository.findByPhone(patientDetails.getPhone()).isPresent()) {
            throw new RuntimeException("Patient with phone " + patientDetails.getPhone() + " already exists");
        }

        patient.setName(patientDetails.getName());
        patient.setAge(patientDetails.getAge());
        patient.setGender(patientDetails.getGender());
        patient.setDisease(patientDetails.getDisease());
        patient.setDoctorAssigned(patientDetails.getDoctorAssigned());
        patient.setAdmittedDate(patientDetails.getAdmittedDate());
        patient.setDischargeDate(patientDetails.getDischargeDate());
        patient.setEmail(patientDetails.getEmail());
        patient.setPhone(patientDetails.getPhone());
        patient.setAdmitted(patientDetails.isAdmitted());
        patient.setAddress(patientDetails.getAddress());
        patient.setDoctorId(patientDetails.getDoctorId());

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return patientRepository.existsById(id);
    }
}