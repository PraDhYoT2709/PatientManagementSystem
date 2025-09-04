package com.pms.patient.repository;

import com.pms.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByEmail(String email);
    
    Optional<Patient> findByPhone(String phone);
    
    List<Patient> findByDoctorId(Long doctorId);
    
    @Query("SELECT p FROM Patient p WHERE LOWER(p.disease) LIKE LOWER(CONCAT('%', :disease, '%'))")
    Page<Patient> findByDiseaseContainingIgnoreCase(@Param("disease") String disease, Pageable pageable);
    
    List<Patient> findByAdmittedTrue();
    
    List<Patient> findByAdmittedFalse();
}