package com.pms.doctor.service;

import com.pms.doctor.entity.Doctor;
import com.pms.doctor.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor) {
        // Check if email already exists
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            throw new RuntimeException("Doctor with email " + doctor.getEmail() + " already exists");
        }
        
        // Check if phone already exists
        if (doctorRepository.findByPhone(doctor.getPhone()).isPresent()) {
            throw new RuntimeException("Doctor with phone " + doctor.getPhone() + " already exists");
        }
        
        return doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public Page<Doctor> getAllDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public Page<Doctor> searchDoctorsByName(String name, Pageable pageable) {
        return doctorRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Doctor> searchDoctorsBySpecialty(String specialty, Pageable pageable) {
        return doctorRepository.findBySpecialtyContainingIgnoreCase(specialty, pageable);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        // Check email uniqueness if changed
        if (!doctor.getEmail().equals(doctorDetails.getEmail()) && 
            doctorRepository.findByEmail(doctorDetails.getEmail()).isPresent()) {
            throw new RuntimeException("Doctor with email " + doctorDetails.getEmail() + " already exists");
        }

        // Check phone uniqueness if changed
        if (!doctor.getPhone().equals(doctorDetails.getPhone()) && 
            doctorRepository.findByPhone(doctorDetails.getPhone()).isPresent()) {
            throw new RuntimeException("Doctor with phone " + doctorDetails.getPhone() + " already exists");
        }

        doctor.setName(doctorDetails.getName());
        doctor.setSpecialty(doctorDetails.getSpecialty());
        doctor.setEmail(doctorDetails.getEmail());
        doctor.setPhone(doctorDetails.getPhone());
        doctor.setQualification(doctorDetails.getQualification());
        doctor.setExperience(doctorDetails.getExperience());
        doctor.setDepartment(doctorDetails.getDepartment());
        doctor.setConsultationFee(doctorDetails.getConsultationFee());
        doctor.setAvailable(doctorDetails.isAvailable());

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return doctorRepository.existsById(id);
    }
}